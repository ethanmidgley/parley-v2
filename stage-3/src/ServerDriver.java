import java.io.IOException;

public class ServerDriver {
  public static void main(String[] args) {

    ClientDirectory directory = new ClientDirectory();
    MessageQueue mq = new TSLinkedListMessageQueue();

    try {
      TCPConnectionListener tcp = new TCPConnectionListener(directory, mq, 8085);
      tcp.start();
    }
    catch (IOException e) {
      System.out.println("TCPListener failed - port may already be in use");
      return;
    }

  }
}
