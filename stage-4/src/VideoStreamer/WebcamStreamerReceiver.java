package VideoStreamer;

import java.net.InetAddress;
import java.io.*;
import java.net.SocketException;
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
  private VideoStreamer vs;

  private boolean[] running;



  public WebcamStreamerReceiver(InetAddress peer) throws IOException, LineUnavailableException {
    //webcam variables
    videoGrabber = new OpenCVFrameGrabber(0);
    videoGrabber.start();
    this.running = new boolean[]{true};
    this.streamPlayer = new StreamPlayer("Webcam",this.running);
    matConverter = new OpenCVFrameConverter.ToMat();

    //construct video streamer and start to listen for incoming webcam video data
    vs = new VideoStreamer(peer,PORT_NUMBER,streamPlayer::addFrame);
    vs.start();
  }

  public void shutdown() {
    this.running[0] = false;
  }

  @Override
  public void run() {

    while (vs.peer == null) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("waiting for connection");
    }

    while(running[0]) {
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
  }
}
