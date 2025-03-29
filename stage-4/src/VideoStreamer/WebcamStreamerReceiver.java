package VideoStreamer;

import java.net.*;
import java.io.*;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.*;

import javax.sound.sampled.*;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class WebcamStreamerReceiver extends Thread {
  private final OpenCVFrameGrabber videoGrabber;
  private final OpenCVFrameConverter.ToMat matConverter;

  private final StreamPlayer player;
  private final short FRAME_RATE = 60;
  private final short AUDIO_CAPTURE_RATE = 60;
  private final short PORT_NUMBER = 7320;
  private final short TERMINATION_PORT_NUMBER = 5000;
  private VideoStreamer vs;

  private TerminableSocket terminableSocket;
  private TerminationEvent event;
  private AtomicBoolean interrupted;

  Thread audioThread;

  public WebcamStreamerReceiver(InetAddress peer) throws IOException, LineUnavailableException {

    this.player = new StreamPlayer("Webcam");
    this.player.start();
    this.matConverter = new OpenCVFrameConverter.ToMat();

    //construct video streamer and start to listen for incoming webcam video data
    this.vs = new VideoStreamer(new AddressReference(peer), PORT_NUMBER, (VideoAudioPair vap) -> {
      player.addFrame(vap);
    });
    this.vs.start();

    //webcam variables
    videoGrabber = new OpenCVFrameGrabber(0);
    videoGrabber.start();

    this.audioThread = new Thread(this::captureAudio);

    this.terminableSocket = new TerminableSocket(new AddressReference(peer), TERMINATION_PORT_NUMBER);
    this.interrupted = new AtomicBoolean(false);

    // FIXME: THIS IS NOT CORRECT
    this.event = () -> {
      if (!interrupted.get()) {
        interrupted.set(true);
        try {
          this.interrupt();
          this.audioThread.interrupt();
          this.videoGrabber.stop();
          this.vs.shutdown();
          this.player.shutdown();
          this.terminableSocket.shutdownPeer();
          this.terminableSocket.shutdown();
        } catch (IOException e) {
          JOptionPane.showMessageDialog(null, "Failed to shutdown some resources, you are good to go the person you were streaming to aren't", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    };


    this.player.bindTerminationEvent(event);
    this.terminableSocket.bindTerminationEvent(event);


  }


  public void shutdown() {
    this.event.terminate();
  }


  public void captureAudio() {

    AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 1, true, false);

    Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
    DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

    // Open and start capturing audio
    try {
      // Open and start capturing audio
      // It's possible to have more control over the chosen audio device with this line:
      // TargetDataLine line = (TargetDataLine)mixer.getLine(dataLineInfo);
      final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
      line.open(audioFormat);
      line.start();

      int bufferSize = (int) audioFormat.getSampleRate() * audioFormat.getFrameSize() / AUDIO_CAPTURE_RATE;
      byte[] audioBuffer = new byte[bufferSize];


      for (; ; ) {
        int bytesRead = line.read(audioBuffer, 0, audioBuffer.length);

        if (bytesRead > 0) {
          // Send the audio data using VideoStreamer
          vs.send(new byte[0], audioBuffer, 0);
        }

        try {
          Thread.sleep(1000 / AUDIO_CAPTURE_RATE);
        } catch (InterruptedException e) {
          break;
        }

      }
    } catch (LineUnavailableException e1) {
      e1.printStackTrace();
    } catch (IOException e) {}
    this.event.terminate();
  }

  @Override
  public void run() {

    //wait until the signal acknowledgement has been received
    while (vs.peer == null) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        break;
      }
      System.out.println("Waiting for connection");
    }

    audioThread.start();

    while (!isInterrupted()) {
      try {
        Frame frame = videoGrabber.grabFrame();

        Mat m = matConverter.convertToMat(frame);

        BytePointer bp = new BytePointer();
        boolean success = opencv_imgcodecs.imencode(".jpg", m, bp);

        if (success) {

          byte[] compressedData = new byte[(int) bp.limit()];
          bp.get(compressedData);
          vs.send(compressedData, new byte[0], 0);
        }
        bp.deallocate();

        try {
          Thread.sleep(1000 / FRAME_RATE);
        } catch (InterruptedException e) {
          break;
        }

        //TODO:update this shit
      } catch (FrameGrabber.Exception e) {
        break;
      } catch (IOException e) {
        break;
      }
    }

    vs.shutdown();
  }
}
