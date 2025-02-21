import java.io.IOException;

public class ServerDriver {
  public static void main(String[] args) {

    ClientDirectory directory = new ClientDirectory();


    try {
      TCPConnectionListener tcp = new TCPConnectionListener(directory, 8085);
      tcp.start();
    }
    catch (IOException e) {
      // TODO: handle the exception, probably because the port is already in use or something, maybe just "Failed to create TCPListener"
      e.printStackTrace();
    }

  }
}
