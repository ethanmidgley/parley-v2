package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_UNCHANGED;

public class FileStreamer extends Thread{
  private final FFmpegFrameGrabber videoGrabber;
  private final CanvasFrame canvasFrame;
  private final File video;

  private final OpenCVFrameConverter.ToMat matConverter;
  private final short FRAME_RATE = 24;
  private final short RECIPIENT_PORT_NUMBER = 7326;
  private VideoStreamer vs;

  public FileStreamer(InetAddress peer,File f) throws SocketException, FrameGrabber.Exception {
    //webcam variables
    this.video = f;
    this.videoGrabber = new FFmpegFrameGrabber(video);
    this.videoGrabber.setFrameRate(FRAME_RATE);
    this.videoGrabber.setAudioChannels(1); //mono
    this.canvasFrame = new CanvasFrame("webcam");
    matConverter = new OpenCVFrameConverter.ToMat();

    //construct video streamer and start to listen for incoming webcam video data
    vs = new VideoStreamer(peer,RECIPIENT_PORT_NUMBER,(VideoAudioPair vap) -> {
      return;
//      Mat receivedMat = opencv_imgcodecs.imdecode(new Mat(vap.video),IMREAD_UNCHANGED);
//      canvasFrame.showImage(matConverter.convert(receivedMat));
    });
    vs.start();
  }

  @Override
  public void run() {
    for(;;) {
      try {
        Frame frame = videoGrabber.grabFrame();
//        frame.samples

        Mat m = matConverter.convertToMat(frame);
        canvasFrame.showImage(frame);

        BytePointer bp = new BytePointer();
        boolean success = opencv_imgcodecs.imencode(".jpg",m,bp);

        if(success) {

          byte[] compressedData = new byte[(int) bp.limit()];
          bp.get(compressedData);
          vs.send(compressedData,new byte[0]);
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
