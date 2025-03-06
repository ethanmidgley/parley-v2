import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class TCPConnectedClient extends ConnectedClient {

  private final ObjectInputStream in;
  private final ObjectOutputStream out;
  private final Socket reading_socket;
  private final Socket writing_socket;
  private String indentifier;
  private final ClientDirectory directory;
  private static final int WRITING_PORT = 8008;


  TCPConnectedClient(Socket socket, ClientDirectory directory, MessageQueue mq) throws IOException {
    super(mq);

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
//        super.dispatch(input);

        switch(input.getType()){

          case USERNAME_PROPAGATE -> { // this is the case where the user is setting up their username to their ip
            if (this.directory.get(input.getContent()) == null) { // check if username doesn't already exist

              this.directory.remove(this.indentifier);
              this.directory.add(input.getContent(), this);
              this.indentifier = input.getContent();

              ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online

              for (String client : client_list) { // loop through users
                Message chatroom_message = new Message("Server", client, this.indentifier + " just joined the server!", new Date(), Type.CHATROOM);
                WritingThread writingThread = new WritingThread(directory, chatroom_message);
                writingThread.start(); // send off the message!! goodbye
              }

              } else {

              Message error_message = new Message("Server",
                      reading_socket.getInetAddress().getHostAddress(),
                      "Error - Name already taken",
                      new Date(),
                      Type.SERVER);
              super.dispatch(error_message);
//              WritingThread writing_thread = new WritingThread(directory, error_message);
//              writing_thread.start();
            }
          }

          case TEXT -> { // this is the case for a regular message
            super.dispatch(input);
//            WritingThread writingThread = new WritingThread(directory, input);
//            writingThread.start();
          }

          case SIGNAL -> { // this is the case for video calls or smn later on
            return;
          }

          case SERVER -> { // this is the case for a server message
            System.out.println("Error - User should not be able to send server messages");
          }

          case CHATROOM -> { // in the case of a message to a chatroom
            ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online

            for (String client : client_list){ // loop through users
              Message chatroom_message = new Message(input.getSender(), client, input.getContent(), input.getSendDate(), Type.CHATROOM); // create a new message with chatroom enum

              if (!(chatroom_message.getRecipient().equals(chatroom_message.getSender()))){ // so we dont send a message back to ourselves
                WritingThread writingThread = new WritingThread(directory, chatroom_message);
                writingThread.start(); // send off the message!! goodbye
              }
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
        ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online

        for (String client : client_list) { // loop through users
          Message chatroom_message = new Message("Server", client, this.indentifier + " just left the server.", new Date(), Type.CHATROOM);
          WritingThread writingThread = new WritingThread(directory, chatroom_message);
          writingThread.start(); // send off the message!! goodbye
        }
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
