import VideoStreamer.WebcamStreamerReceiver;

import java.io.IOException;
import java.net.InetAddress;

public class WebcamTestDriver {
  public static void main(String[] args) throws IOException {
    InetAddress ip = null;
    WebcamStreamerReceiver wsb = new WebcamStreamerReceiver(ip);
    wsb.start();
  }
}
