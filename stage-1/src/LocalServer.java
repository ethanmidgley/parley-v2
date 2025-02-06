import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalServer extends Thread {

  private final int server_port;
  public LocalServer(int port) {
    super();
    this.server_port = port;
  }

  public void run() {
    try {
      ServerSocket server = new ServerSocket(server_port);

      Socket client = server.accept();

      PrintWriter out = new PrintWriter(client.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

      String input;
      while ((input = in.readLine())!= null) {
        System.out.printf("\033[2K\r%s: %s\n", client.getInetAddress().getHostAddress(), input);
        System.out.print("You: ");
        // Send back the response
        out.println(input);
      }
    } catch (IOException e) {
      System.err.println("I/O Error");
      e.printStackTrace();
      System.exit(1);
    }
  }

}
