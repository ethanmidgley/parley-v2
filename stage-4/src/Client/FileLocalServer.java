package Client;

import Message.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.UUID;

public class FileLocalServer extends Thread {

  private final int server_port;
  private final FileReceivedEvent fileReceivedEvent;
  public OutputStream os;

  public FileLocalServer(int port, FileReceivedEvent fileReceivedEvent) {
    super();
    this.server_port = port;
    this.fileReceivedEvent = fileReceivedEvent;
  }

  public void run() {
    try {
      ServerSocket server = new ServerSocket(server_port);

      for (;;) {
        Socket client = server.accept();

        UUID uuid = UUID.randomUUID();
        File f = new File(uuid.toString());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
        InputStream is = client.getInputStream();

        byte[] content = new byte[10000];

        int bytesRead = 0;
        while ((bytesRead = is.read(content)) != 1) {
          bos.write(content, 0, bytesRead);
        }
        bos.flush();

        fileReceivedEvent.trigger(f);
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Lost connection to server.");
    }
  }
}
