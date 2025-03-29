import VideoStreamer.FileStreamer;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;


public class FileStreamingTest {
  public static void main(String[] args) throws IOException, LineUnavailableException {
    File video = new File("./a.mp4");
    InetAddress peer = Inet4Address.getByName("127.0.0.1");
    FileStreamer fs = new FileStreamer(peer,video);
    fs.start();
  }
}
