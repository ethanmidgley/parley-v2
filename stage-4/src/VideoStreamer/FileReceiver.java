package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.net.InetAddress;
import java.net.SocketException;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_UNCHANGED;

public class FileReceiver {

  private final CanvasFrame canvasFrame;
  private final OpenCVFrameConverter.ToMat matConverter;
  private final InetAddress peer;

  private final short PORT_NUMBER = 7326;
  private VideoStreamer vs;

  public FileReceiver() throws SocketException, FrameGrabber.Exception {
    //webcam variables
    this.canvasFrame = new CanvasFrame("webcam");
    matConverter = new OpenCVFrameConverter.ToMat();

    //set peer to null will be initid during listening
    //init to Inet4Address

    peer = null;

    //construct video streamer and start to listen for incoming webcam video data
    vs = new VideoStreamer(peer,PORT_NUMBER,(VideoAudioPair vap) -> {
      Mat receivedMat = opencv_imgcodecs.imdecode(new Mat(vap.video),IMREAD_UNCHANGED);
      canvasFrame.showImage(matConverter.convert(receivedMat));
    });
    vs.start();
  }

}
