package Client;

import Message.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalServer extends Thread {

  private final int server_port;
  private final MessageReceivedEvent messageReceivedEvent;

  public LocalServer(int port, MessageReceivedEvent messageReceivedEvent) {
    super();
    this.server_port = port;
    this.messageReceivedEvent = messageReceivedEvent;
  }

  public void run() {
    try {
      ServerSocket server = new ServerSocket(server_port);

      Socket client = server.accept();

      ObjectInputStream in = new ObjectInputStream(client.getInputStream());
      Message input;
      while ((input = (Message) in.readObject())!= null) {
        System.out.println(input);
        messageReceivedEvent.trigger(input);
      }
      server.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Lost connection to server.");
      System.exit(0);
    } catch (ClassNotFoundException e) {
      System.out.println("Message.Message data corrupted");
    }
  }
}