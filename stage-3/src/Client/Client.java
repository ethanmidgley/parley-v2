package Client;

import Message.Message;

import java.io.*;
import java.net.Socket;

public class Client {

  private final int LISTENING_PORT = 8008;
  private final int WRITING_PORT = 8085;

  private Socket writingSocket;
  private ObjectOutputStream out;

  public Client(MessageRecievedEvent event) {
    LocalServer server = new LocalServer(LISTENING_PORT, event);
    server.start();
  }

  public void connectToServer(String ip) throws IOException {
    writingSocket = new Socket(ip, WRITING_PORT);
    out = new ObjectOutputStream(writingSocket.getOutputStream());
  }

  public void sendMessage(Message message) {
    try {
      out.writeObject(message);
      out.flush();
    } catch (IOException e) {
      System.out.println("Failed to send message");
      return;
    }
  }
}
