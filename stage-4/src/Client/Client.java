package Client;

import Message.Message;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

  private final int LISTENING_PORT = 8008;
  private final int WRITING_PORT = 8085;
  private final int FILE_PORT = 8009;

  private Socket writingSocket;
  private ObjectOutputStream out;

  public Client(MessageRecievedEvent event, FileReceivedEvent file_event) {
    LocalServer server = new LocalServer(LISTENING_PORT, event);
    FileLocalServer fileServer = new FileLocalServer(FILE_PORT, file_event);
    server.start();
    fileServer.start();
  }

  public void bindMessageRecieve(MessageRecievedEvent e) {
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

  public void sendFile(InetAddress ip, File file){
    try {
      FileInputStream fis = new FileInputStream(file);
      Socket socket = new Socket(ip, FILE_PORT);

      BufferedOutputStream bout = new BufferedOutputStream(socket.getOutputStream());
      BufferedInputStream bin = new BufferedInputStream(fis);

      byte[] content = new byte[10000];
      int bytesRead = 0;
      while ((bytesRead = bin.read(content)) != -1) {
        bout.write(content, 0, bytesRead);
      }
      bout.flush();

    } catch (IOException e) {
      System.out.println("Failed to send file");
    }

  }
}
