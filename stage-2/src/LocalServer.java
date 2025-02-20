import java.io.*;
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

      ObjectInputStream in = new ObjectInputStream(client.getInputStream());
      Message input;
      while ((input = (Message) in.readObject())!= null) {
        System.out.printf("\033[2K\r%s: %s\n", input.getSender(), input.getContent());
      }
    } catch (IOException e) {
      System.out.println("Connection closed.");
      System.exit(0);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

}
