package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.ShortBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileStreamer extends Thread {

  private final AddressReference peer;



  private final FFmpegFrameGrabber videoGrabber;
  private final File video;
  private final OpenCVFrameConverter.ToMat matConverter;

  private final short RECIPIENT_PORT_NUMBER = 7325;
  private final short PORT_NUMBER = 7326;
  private final short TERMINATION_PORT_NUMBER = 4000;

  private final StreamPlayer player;
  private final VideoStreamer vs;

  private final TerminableSocket terminableSocket;
  private final TerminationEvent event;
  private AtomicBoolean interrupted;

  public FileStreamer(InetAddress peer, File f) throws IOException, LineUnavailableException {

    // set up peer and video
    this.peer = new AddressReference(peer);
    this.video = f;

    // set up FFMPEG to get from video + grabber variables
    this.matConverter = new OpenCVFrameConverter.ToMat();
    this.videoGrabber = new FFmpegFrameGrabber(video);
    this.videoGrabber.setFrameRate(10);
    this.videoGrabber.setAudioChannels(1); //mono
    this.videoGrabber.start();



    this.vs = new VideoStreamer(this.peer, RECIPIENT_PORT_NUMBER);
    this.player = new StreamPlayer("Video Stream");
    this.terminableSocket = new TerminableSocket(this.peer, TERMINATION_PORT_NUMBER);

    this.interrupted = new AtomicBoolean(false);



    this.event = () -> {
      if (!interrupted.get()) {
        interrupted.set(true);
        try {
          this.interrupt();
          this.vs.shutdown();
          this.player.shutdown();
          this.videoGrabber.stop();
          this.terminableSocket.shutdownPeer();
          this.terminableSocket.shutdown();
        } catch (IOException e) {
          JOptionPane.showMessageDialog(null, "Failed to shutdown some resources, you are good to go the person you were streaming to aren't", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    };


    this.player.bindTerminationEvent(event);
    this.terminableSocket.bindTerminationEvent(event);


    this.player.start();


    // Set up a listener to listen for disconnect message
    this.terminableSocket.start();



  }


  public void run() {

    while (!this.isInterrupted()) {

      try {

        Frame frame = videoGrabber.grabFrame();
        if (frame != null) {


          if (frame.samples != null) {

            ShortBuffer shortBuffer = (ShortBuffer) frame.samples[0];
            byte[] audioBytes = new byte[shortBuffer.remaining() * 2]; // 2 bytes per short
            for (int i = 0; i < shortBuffer.remaining(); i++) {
              short sample = shortBuffer.get(i);
              audioBytes[i * 2] = (byte) (sample & 0xFF); // Lower byte
              audioBytes[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF); // Higher byte
            }

            vs.send(new byte[0], audioBytes, frame.timestamp);
            player.addFrame(new VideoAudioPair(frame.timestamp, new byte[0], audioBytes));
          }

          if (frame.image != null) {


            Mat m = matConverter.convertToMat(frame);

            try (BytePointer bp = new BytePointer()) {
              boolean success = opencv_imgcodecs.imencode(".jpg", m, bp);

              if (success) {
                byte[] compressedData = new byte[(int) bp.limit()];
                bp.get(compressedData);
                vs.send(compressedData, new byte[0], 0);
                player.addFrame(new VideoAudioPair(frame.timestamp, compressedData, new byte[0]));
              }
            }

            m.release();

          }


          Thread.sleep(10);
        }

        //TODO:update this shit
      } catch (FrameGrabber.Exception e) {
        e.printStackTrace();
        break;
      }
      catch (IOException e) {
        e.printStackTrace();
        break;
      } catch (InterruptedException e) {
        break;
      }
    }

    this.event.terminate();
  }


  public void shutdown() {
    this.event.terminate();
  }


}
