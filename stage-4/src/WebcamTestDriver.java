import VideoStreamer.WebcamStreamerReciever;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class WebcamTestDriver {
  public static void main(String[] args) throws IOException {
    InetAddress ip = null;
    WebcamStreamerReciever wsb = new WebcamStreamerReciever(ip,7325);
    wsb.start();
  }
}
