import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPConnectedClient extends ConnectedClient {

  private final BufferedReader in;
  private final PrintWriter out;
  private final Socket reading_socket;
  private final Socket writing_socket;
  private final ClientDirectory directory;

  private static final int WRITING_PORT = 6912;


  TCPConnectedClient(Socket socket, ClientDirectory directory) throws IOException {

    this.directory = directory;

    this.reading_socket = socket;
    this.in = new BufferedReader(new InputStreamReader(reading_socket.getInputStream()));

    this.writing_socket = new Socket(socket.getInetAddress().getHostAddress(), WRITING_PORT);
    this.out = new PrintWriter(writing_socket.getOutputStream(), true);


    // Client created now let's add it to the directory with just the ip address as their name at the moment
    this.directory.add(socket.getInetAddress().getHostAddress(), this);
  }

  public void listen()  {

    String input;
    while (true) {
      try {
        if (!((input = in.readLine())!= null)) break;


      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      // We will here have to create a writing thread
//      System.out.printf("\033[2K\r%s: %s\n", socket.getInetAddress().getHostAddress(), input);
      // Send back the response
//      out.println(input);
    }


  }

  public void send(String message) {
    this.out.println(message);
  }
}
