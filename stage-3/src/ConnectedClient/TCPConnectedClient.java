package ConnectedClient;

import ClientDirectory.ClientDirectory;
import Message.Message;
import Message.Type;
import MessageQueue.MessageQueue;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class TCPConnectedClient extends ConnectedClient {

  private final ObjectInputStream in;
  private final ObjectOutputStream out;
  private final Socket reading_socket;
  private final Socket writing_socket;
  private String identifier;
  private final ClientDirectory directory;
  private static final int WRITING_PORT = 8008;


  public TCPConnectedClient(Socket socket, ClientDirectory directory, MessageQueue mq) throws IOException {
    super(mq);

    this.directory = directory;

    this.reading_socket = socket;
    this.in = new ObjectInputStream(reading_socket.getInputStream());

    this.writing_socket = new Socket(socket.getInetAddress().getHostAddress(), WRITING_PORT);
    this.out = new ObjectOutputStream(writing_socket.getOutputStream());

    // Client.Client created now let's add it to the directory with just the ip address as their name at the moment
    this.identifier = socket.getInetAddress().getHostAddress();
    this.directory.add(identifier, this);
  }

  public void listen()  {

    Message input;
    while (true) {
      try {
        input = (Message) in.readObject();
      } catch (ClassNotFoundException e) {
        // TODO: Split in to two exceptions, notes on notion
        System.out.println("Message.Message data corrupted");
      }
      catch (IOException e ){
        // The stream has closed so just kick the user
        this.directory.remove(this.identifier);
//        ServerDriver.UpdateOnlineUsers("-");
        ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online

        for (String client : client_list) { // loop through users
          Message chatroom_message = new Message("Server", client, this.identifier + " just left the server.", new Date(), Type.CHATROOM);
          super.dispatch(chatroom_message); // send off the message!! goodbye
        }
        return;
      }
    }

  }

  public void send(Message message){
    synchronized (this.out) {
      try {
        this.out.writeObject(message);
        this.out.flush();
      } catch (IOException e) {
        // TODO: handle this exception instead as a client disconnected
        this.directory.remove(reading_socket.getInetAddress().getHostAddress());
      }
    }
  }
}
