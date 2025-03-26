import VideoStreamer.WebcamStreamerReceiver;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.InetAddress;

public class WebcamTestDriver {
  public static void main(String[] args) throws IOException, LineUnavailableException {
    InetAddress ip = null;
    WebcamStreamerReceiver wsb = new WebcamStreamerReceiver(ip);
    wsb.start();
  }
}
