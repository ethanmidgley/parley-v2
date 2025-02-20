import java.io.IOException;

public class ServerDriver {
  public static void main(String[] args) throws IOException {

    ClientDirectory directory = new ClientDirectory();

    TCPConnectionListener tcp = new TCPConnectionListener(directory, 8085);
    tcp.start();

  }
}
