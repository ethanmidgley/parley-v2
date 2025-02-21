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
        // TODO: both server.accept and new TCPConnectedClient throw an IO exception
        // TODO: they need to be handled separately
        // TODO: if .accept throws exception then the listening socket is no longer open
        // TODO: if new TCPConnectectClient throws exception then we failed to establish two way communication with client
        throw new RuntimeException(e);
      }
    }
  }
}
