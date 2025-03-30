package Client;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        try {

          Socket client = server.accept();

          InputStream is = client.getInputStream();
          DataInputStream dis =  new DataInputStream(is);
          String filename = dis.readUTF();

          Path filePath = Paths.get("files", filename);
          File f = new File(filePath.toAbsolutePath().toString());
          f.getParentFile().mkdirs();

          BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));

          byte[] content = new byte[10000];

          int bytesRead = 0;
          while ((bytesRead = is.read(content)) > -1) {
            bos.write(content, 0, bytesRead);
          }
          bos.flush();
          bos.close();

          fileReceivedEvent.trigger(f);

        } catch (IOException e) {
          break;
        }
      }
      server.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Lost connection to server.");
    }
  }
}
