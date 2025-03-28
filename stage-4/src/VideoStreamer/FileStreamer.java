package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ShortBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileStreamer extends Thread{

  private final InetAddress peer;
  private final FFmpegFrameGrabber videoGrabber;
  private final File video;

  private final OpenCVFrameConverter.ToMat matConverter;
  private final short FRAME_RATE = 60;
  private final short RECIPIENT_PORT_NUMBER = 7325;
  private final short PORT_NUMBER = 7326;
  private final short TERMINATION_PORT_NUMBER = 4000;
  private final VideoStreamer vs;
  private final StreamPlayer p;
  private final DatagramSocket terminationSocket;
  private AtomicBoolean running;


  public FileStreamer(InetAddress peer,File f) throws IOException, LineUnavailableException {
    //webcam variables
    this.video = f;
    this.peer = peer;
    this.matConverter = new OpenCVFrameConverter.ToMat();
    this.videoGrabber = new FFmpegFrameGrabber(video);
    this.videoGrabber.setFrameRate(10);
    this.videoGrabber.setAudioChannels(1); //mono
    this.videoGrabber.start();
    this.running = new AtomicBoolean(true);
    this.terminationSocket = new DatagramSocket(TERMINATION_PORT_NUMBER);


    this.p = new StreamPlayer("Video Stream", running);
    p.start();

    vs = new VideoStreamer(peer, PORT_NUMBER, RECIPIENT_PORT_NUMBER, p::addFrame, this.running);
    vs.start();

  }

//  private void sendTermination() {
//    System.out.println("sending termination to file streamer, file receiver line 57");
//    try {
//      //account for peer being null
//      DatagramSocket das = new DatagramSocket(11000);
//      DatagramPacket dap = new DatagramPacket(new byte[255], 255,peer,TERMINATION_PORT_NUMBER);
//      das.send(dap);
//      das.close();
//    } catch (SocketException e) {
//      throw new RuntimeException(e);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }

  public void shutdown() {
    System.out.println("shutting down file streamer: fileStreamer line 72");
    this.running.set(false);
    try {
      System.out.println("shutting down the video steamer");
      vs.shutdown();
      p.shutdown();
      System.out.println("shutting down the video grabber");
      videoGrabber.close();
//      sendTermination();
      System.out.println("closing termination socket");
      terminationSocket.close();
    } catch (FrameGrabber.Exception e) {
      System.out.println("FRAME GRABBER failed to close: LINE 54: FileStreamer.java");;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    System.out.println("thread stopped running");
  }

  //TODO add a while loop for validating signal
  private void termination_listener() {
    //this thread should run for until a small packet is received and this.running will be set to false
    new Thread(() -> {
      try {
        while(true) {
          byte[] buffer = new byte[255];
          DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
          this.terminationSocket.receive(datagramPacket);
          if(datagramPacket.getAddress().equals(peer)) {
            System.out.println("received termination instruction from receiver: file streamer line 86");
            this.shutdown();
            break;
          }
          else {
            System.out.println("fuck you saids the fuck you guy");
          }
        }
      } catch (SocketException e) {
        System.out.println("terminationSocket closed by running flag, FileStreamer line 103");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      System.out.println("im at the end of waiting for a termination signal");
    }).start();
  }

  @Override
  public void run() {
    //how many seconds to midnight?
    this.termination_listener();

    while(running.get()) {
      try {

        Frame frame = videoGrabber.grabFrame();

        if (frame.samples != null) {


          ShortBuffer shortBuffer = (ShortBuffer) frame.samples[0];
          byte[] audioBytes = new byte[shortBuffer.remaining() * 2]; // 2 bytes per short
          for (int i = 0; i < shortBuffer.remaining(); i++) {
            short sample = shortBuffer.get(i);
            audioBytes[i * 2] = (byte) (sample & 0xFF); // Lower byte
            audioBytes[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF); // Higher byte
          }


//          AudioFormat format = new AudioFormat(videoGrabber.getSampleRate(), 16, videoGrabber.getAudioChannels(), true, false);

//          byte[] data = AudioEncoder.encode(audioBytes, format);



          vs.send(new byte[0], audioBytes, frame.timestamp);
          p.addFrame(new VideoAudioPair(frame.timestamp, new byte[0], audioBytes));


        }

        if (frame.image != null) {


          Mat m = matConverter.convertToMat(frame);

          BytePointer bp = new BytePointer();
          boolean success = opencv_imgcodecs.imencode(".jpg",m,bp);


          if(success) {

            byte[] compressedData = new byte[(int) bp.limit()];
            bp.get(compressedData);

            vs.send(compressedData,new byte[0], frame.timestamp);
            p.addFrame(new VideoAudioPair(frame.timestamp, compressedData, new byte[0]));
          }
          bp.deallocate();

        }

//          try {
//            Thread.sleep(1000/FRAME_RATE);
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }

        //TODO:update this shit
      } catch (FrameGrabber.Exception e) {
        throw new RuntimeException(e);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    this.shutdown();
    System.out.println("file streamer terminated");
  }
}
