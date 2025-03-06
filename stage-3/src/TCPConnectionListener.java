import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPConnectionListener extends ConnectionListener {

  private final ServerSocket server;
  private final ClientDirectory directory;

  private final MessageQueue mq;

  public TCPConnectionListener(ClientDirectory directory, MessageQueue mq, int port) throws IOException {
    this.mq = mq;
    this.server = new ServerSocket(port);
    this.directory = directory;
  }

  public void listen()  {
    while (true) {

      Socket client = null;

      try {
        // TODO: if .accept throws exception then the listening socket is no longer open
        client = server.accept();
      } catch(IOException e) {
        System.out.println("Server listening socket closed...");;
      }

      if(client != null) {
        try {
          ConnectedClient c = new TCPConnectedClient(client, directory,mq);
          c.start();
        } catch (IOException e) {
          // TODO: if new TCPConnectectClient throws exception then we failed to establish two way communication with client
          System.out.println("failed to establish Client connection to the server...");
        }
      }
    }
  }
}
