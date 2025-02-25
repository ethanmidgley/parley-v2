import java.io.IOException;

public class ServerDriver {
  public static void main(String[] args) {

    ClientDirectory directory = new ClientDirectory();


    try {
      TCPConnectionListener tcp = new TCPConnectionListener(directory, 8085);
      tcp.start();
    }
    catch (IOException e) {
      System.out.println("TCPListener failed - port may already be in use");
      return;
    }

  }
}
