package VideoStreamer;

import java.net.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.*;

import javax.sound.sampled.LineUnavailableException;

import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class WebcamStreamerReceiver extends Thread {
  private final OpenCVFrameGrabber videoGrabber;
  private final StreamPlayer streamPlayer;
  private final OpenCVFrameConverter.ToMat matConverter;

  private final short FRAME_RATE = 60;
  private final short PORT_NUMBER = 7320;
  private final short TERMINATION_PORT_NUMBER = 5000;
  private VideoStreamer vs;

  private final DatagramSocket terminationSocket;
  private final DatagramPacket terminationSignal;
  private AtomicBoolean running;


  public WebcamStreamerReceiver(InetAddress peer) throws IOException, LineUnavailableException {
    //webcam variables
    videoGrabber = new OpenCVFrameGrabber(0);
    videoGrabber.start();
    this.terminationSocket = new DatagramSocket(TERMINATION_PORT_NUMBER);
    this.terminationSignal = new DatagramPacket(new byte[255], 255, peer, TERMINATION_PORT_NUMBER);
    this.running = new AtomicBoolean(true);
    this.streamPlayer = new StreamPlayer("Webcam",this.running);
    matConverter = new OpenCVFrameConverter.ToMat();

    //construct video streamer and start to listen for incoming webcam video data
    vs = new VideoStreamer(peer,PORT_NUMBER,streamPlayer::addFrame,running);
    vs.start();
  }

  private void sendTermination() {
    try {
      //account for peer being null
      terminationSocket.send(terminationSignal);
    } catch (SocketException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void termination_listener() {
    //this thread should run for until a small packet is received and this.running will be set to false
    new Thread(() -> {
      try {
        byte[] buffer = new byte[255];
        DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
        this.terminationSocket.receive(datagramPacket);
        //using vs.peer bc its almost 1am
        if(datagramPacket.getAddress().equals(this.vs.peer)) {
          this.running.set(false);
        }
        else {
          System.out.println("someone from the outside is trying to kill the connection");
        }
      } catch (SocketException e) {
        e.printStackTrace();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).start();
  }

  public void shutdown() {
    this.running.set(false);
    try {
      vs.shutdown();
      sendTermination();
      terminationSocket.close();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {

    //wait until the signal acknowledgement has been received
    while (vs.peer == null) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("waiting for connection");
    }

    //termination listener
    this.termination_listener();

    while(running.get()) {
      try {
        Frame frame = videoGrabber.grabFrame();

        Mat m = matConverter.convertToMat(frame);

        BytePointer bp = new BytePointer();
        boolean success = opencv_imgcodecs.imencode(".jpg",m,bp);

        if(success) {

          byte[] compressedData = new byte[(int) bp.limit()];
          bp.get(compressedData);
          vs.send(compressedData,new byte[0], frame.timestamp);
        }
        bp.deallocate();

        try {
          Thread.sleep(1000/ FRAME_RATE );
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        //TODO:update this shit
      } catch (FrameGrabber.Exception e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    System.out.println("running: " + running);
    try {
      vs.shutdown();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
