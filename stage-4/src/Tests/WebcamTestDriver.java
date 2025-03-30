package Tests;

import VideoStreamer.WebcamStreamerReceiver;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

public class WebcamTestDriver {
  public static void main(String[] args) throws IOException, LineUnavailableException {
    InetAddress ip = Inet4Address.getLocalHost();
    WebcamStreamerReceiver wsb = new WebcamStreamerReceiver(ip);
    wsb.start();
  }
}
