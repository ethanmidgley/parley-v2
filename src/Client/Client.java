package Client;

import Message.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Client {

  private final int LISTENING_PORT = 8008;
  private final int WRITING_PORT = 8085;
  private final int FILE_PORT = 12678;

  // Here we can store callbacks so we can listen for message replies
  private final Map<UUID, MessageReceivedEvent> callbacks;

  private Socket writingSocket;
  private ObjectOutputStream out;

  public Client() {
    this.callbacks = new HashMap<>();
  }

  public void bindMessageReceivedEvent(MessageReceivedEvent e) {

    LocalServer server = new LocalServer(LISTENING_PORT, (Message recievedMessage) -> {

      MessageReceivedEvent callbackEvent = callbacks.get(recievedMessage.getId());

      if (callbackEvent != null) {
        callbackEvent.trigger(recievedMessage);
      } else {
        e.trigger(recievedMessage);
      }

    });
    server.start();

  }

  public void bindFileReceivedEvent(FileReceivedEvent e) {
    FileLocalServer fileServer = new FileLocalServer(FILE_PORT, e);
    fileServer.start();
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


  public void sendMessage(Message message, MessageReceivedEvent callback) {
    try {
      callbacks.put(message.getId(), callback);
      out.writeObject(message);
      out.flush();
    } catch (IOException e) {
      System.out.println("Failed to send message");
      return;
    }
  }


  public void sendFile(InetAddress ip, File file){
    try {
      FileInputStream fis = new FileInputStream(file);
      Socket socket = new Socket(ip, FILE_PORT);

      BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream());
      BufferedInputStream bis = new BufferedInputStream(fis);
      DataOutputStream dos = new DataOutputStream(bout);
      dos.writeUTF(file.getName());

      byte[] content = new byte[10000];
      int bytesRead = 0;
      while ((bytesRead = bis.read(content)) != -1) {
        dos.write(content, 0, bytesRead);
      }
      dos.flush();
      bout.close();
      dos.close();
      bis.close();
      fis.close();

      socket.close();


    } catch (IOException e) {
      System.out.println("Failed to send file");
    }

  }
}
