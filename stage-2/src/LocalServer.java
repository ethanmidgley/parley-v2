import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalServer extends Thread {

  private final int server_port;
  private final MessageRecievedEvent messageRecievedEvent;

  public LocalServer(int port, MessageRecievedEvent messageRecievedEvent) {
    super();
    this.server_port = port;
    this.messageRecievedEvent = messageRecievedEvent;
  }

  public void run() {
    try {
      ServerSocket server = new ServerSocket(server_port);

      Socket client = server.accept();

      ObjectInputStream in = new ObjectInputStream(client.getInputStream());
      Message input;
      while ((input = (Message) in.readObject())!= null) {
        messageRecievedEvent.trigger(input);
      }
    } catch (IOException e) {
      System.out.println("Lost connection to server.");
      System.exit(0);
    } catch (ClassNotFoundException e) {
      // TODO: Handle error message
      throw new RuntimeException(e);
    }
  }

}
