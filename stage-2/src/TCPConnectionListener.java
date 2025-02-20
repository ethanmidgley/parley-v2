import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPConnectionListener extends ConnectionListener {

  private final ServerSocket server;
  private final ClientDirectory directory;

  public TCPConnectionListener(ClientDirectory directory, int port) throws IOException {
    this.server = new ServerSocket(port);
    this.directory = directory;
  }

  public void listen()  {
    while (true) {
      try {

        Socket client = server.accept();
        ConnectedClient c = new TCPConnectedClient(client, directory);
        c.start();

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
