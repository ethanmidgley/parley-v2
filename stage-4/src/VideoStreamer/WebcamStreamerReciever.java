package VideoStreamer;

import java.net.InetAddress;
import java.io.*;
import java.net.SocketException;
import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;

public class WebcamStreamerReciever extends Thread {
  private final OpenCVFrameGrabber videoGrabber;
  private final CanvasFrame canvasFrame;
  private final OpenCVFrameConverter.ToMat matConverter;

  private final short FRAME_RATE = 60;
  private final short PORT_NUMBER = 7325;
  private VideoStreamer vs;

  public WebcamStreamerReciever(InetAddress peer) throws SocketException, FrameGrabber.Exception {
    //webcam variables
    videoGrabber = new OpenCVFrameGrabber(0);
    videoGrabber.start();
    this.canvasFrame = new CanvasFrame("webcam");
    matConverter = new OpenCVFrameConverter.ToMat();

    //construct video streamer and start to listen for incoming webcam video data
    vs = new VideoStreamer(peer,PORT_NUMBER,(VideoAudioPair vap) -> {
      Mat receivedMat = opencv_imgcodecs.imdecode(new Mat(vap.video),IMREAD_UNCHANGED);
      canvasFrame.showImage(matConverter.convert(receivedMat));
    });
    vs.start();
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

    for(;;) {
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
