import VideoStreamer.FileStreamer;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;


public class FileStreamingTest {
  public static void main(String[] args) throws IOException {
    File video = new File("path here");
    InetAddress peer = Inet4Address.getByName("hostname_here");
    FileStreamer fs = new FileStreamer(peer,video);
    fs.start();
  }
}
