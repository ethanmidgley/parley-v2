import java.io.*;
import java.net.Socket;

public class TCPConnectedClient extends ConnectedClient {

  private final ObjectInputStream in;
  private final ObjectOutputStream out;
  private final Socket reading_socket;
  private final Socket writing_socket;
  private final ClientDirectory directory;

  private static final int WRITING_PORT = 8008;


  TCPConnectedClient(Socket socket, ClientDirectory directory) throws IOException {

    this.directory = directory;

    this.reading_socket = socket;
    this.in = new ObjectInputStream(reading_socket.getInputStream());

    this.writing_socket = new Socket(socket.getInetAddress().getHostAddress(), WRITING_PORT);
    this.out = new ObjectOutputStream(writing_socket.getOutputStream());

    // Client created now let's add it to the directory with just the ip address as their name at the moment
    this.directory.add(socket.getInetAddress().getHostAddress(), this);
  }

  public void listen()  {

    Message input;
    while (true) {
      try {

        if ((input = (Message) in.readObject()) == null) break;

        WritingThread writingThread = new WritingThread(directory, input);
        writingThread.start();

      } catch (IOException | ClassNotFoundException e) {
        // TODO: Split in to two exceptions, notes on notion
        throw new RuntimeException(e);
      }
    }

  }

  public void send(Message message){
    try {
      this.out.writeObject(message);
      this.out.flush();
    } catch (IOException e) {
      // TODO: handle this exception instead as a client disconnected
      throw new RuntimeException(e);
    }
  }
}
