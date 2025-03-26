package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_UNCHANGED;

public class FileReceiver {

  private final InetAddress peer;
  private final short PORT_NUMBER = 7325;
  private VideoStreamer vs;
  private StreamPlayer player;

  public FileReceiver() throws IOException, LineUnavailableException {

    this.player = new StreamPlayer("Receive stream");

    //set peer to null will be initid during listening
    //init to Inet4Address
    peer = null;
    //construct video streamer and start to listen for incoming webcam video data
    player.start();
    vs = new VideoStreamer(peer,PORT_NUMBER,(VideoAudioPair vap) -> {
      if (vap.video.length > 0) {
        System.out.println("hasdlfkjasd;lfkjsda;lkj");
      }
      player.addFrame(vap);
    });
    vs.start();
  }

}
