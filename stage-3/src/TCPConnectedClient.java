import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TCPConnectedClient extends ConnectedClient {

  private final ObjectInputStream in;
  private final ObjectOutputStream out;
  private final Socket reading_socket;
  private final Socket writing_socket;
  private String indentifier;
  private final ThreadSafeClientDirectory directory;
  private static final int WRITING_PORT = 8008;
  private Lock lock = new ReentrantLock();


  TCPConnectedClient(Socket socket, ThreadSafeClientDirectory directory) throws IOException {

    this.directory = directory;

    this.reading_socket = socket;
    this.in = new ObjectInputStream(reading_socket.getInputStream());

    this.writing_socket = new Socket(socket.getInetAddress().getHostAddress(), WRITING_PORT);
    this.out = new ObjectOutputStream(writing_socket.getOutputStream());

    // Client created now let's add it to the directory with just the ip address as their name at the moment
    this.indentifier = socket.getInetAddress().getHostAddress();
    this.directory.add(indentifier, this);
  }

  public void listen()  {

    Message input;
    while (true) {
      try {

        input = (Message) in.readObject();

        switch(input.getType()){

          case USERNAME_PROPAGATE -> { // this is the case where the user is setting up their username to their ip
            if (this.directory.get(input.getContent()) == null) { // check if username doesn't already exist

              this.directory.remove(this.indentifier);
              this.directory.add(input.getContent(), this);
              this.indentifier = input.getContent();
            } else {

              Message error_message = new Message("Server",
                      reading_socket.getInetAddress().getHostAddress(),
                      "Error - Name already taken",
                      new Date(),
                      Type.SERVER);
              WritingThread writing_thread = new WritingThread(directory, error_message);
              writing_thread.start();
            }
          }

          case TEXT -> { // this is the case for a regular message
            WritingThread writingThread = new WritingThread(directory, input);
            writingThread.start();
          }

          case SIGNAL -> { // this is the case for video calls or smn later on
            return;
          }

          case SERVER -> { // this is the case for a server message
            System.out.println("Error - User should not be able to send server messages");
          }

          case UPDATE_USERNAME -> { // this is the case to update username of a user    
            lock.lock();
            try{
              if (this.directory.get(input.getContent()) == null) {
                System.out.println("this is a unique name");
                // ThreadSafeClientDirectory.changeUsername(input.getSender(),input.getContent());
                updateThread thread = new updateThread(directory, input);
                thread.start();
              }else{
                Message error_message = new Message("Server",
                  reading_socket.getInetAddress().getHostAddress(),
                  "Error - Name already taken",
                  new Date(),
                  Type.SERVER);
                  WritingThread writing_thread = new WritingThread(directory, error_message);
                writing_thread.start();
              }
      
            }finally{
              lock.unlock();
            }
            
          }

        }

      } catch (ClassNotFoundException e) {
        // TODO: Split in to two exceptions, notes on notion
        System.out.println("Message data corrupted");
      }
      catch (IOException e ){
        // The stream has closed so just kick the user
        this.directory.remove(this.indentifier);
        return;
      }
    }

  }

  public void send(Message message){
    try {
      this.out.writeObject(message);
      this.out.flush();
    } catch (IOException e) {
      // TODO: handle this exception instead as a client disconnected
      this.directory.remove(reading_socket.getInetAddress().getHostAddress());
    }
  }
}
