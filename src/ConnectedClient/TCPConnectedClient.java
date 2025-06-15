package ConnectedClient;

import ClientDirectory.ClientDirectory;
import Message.Message;
import Message.UsernameMessage;
import Message.Type;
import MessageQueue.MessageQueue;
import OnlineCount.OnlineCount;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class TCPConnectedClient extends ConnectedClient {

  private final ObjectInputStream in;
  private final ObjectOutputStream out;
  private final Socket reading_socket;
  private final Socket writing_socket;
  private final ClientDirectory directory;
  private static final int WRITING_PORT = 8008;


  public TCPConnectedClient(Socket socket, ClientDirectory directory, MessageQueue mq, OnlineCount onlineCount) throws IOException {
    super(socket.getInetAddress(), mq,onlineCount);

    this.directory = directory;

    this.reading_socket = socket;
    this.in = new ObjectInputStream(reading_socket.getInputStream());

    this.writing_socket = new Socket(socket.getInetAddress().getHostAddress(), WRITING_PORT);
    this.out = new ObjectOutputStream(writing_socket.getOutputStream());

    // Client.Client created now let's add it to the directory with just the ip address as their name at the moment
    this.directory.add(super.getIdentifier(), this);

    super.incrementPublishOnlineCount();

  }

  // ONLINE USERS BROADCAST
//              ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online
//
//              for (String client : client_list) { // loop through users
//                Message chatroom_message = new Message("Server", client, input.getSender() + " just joined the server!", new Date(), Type.CHATROOM);
//                super.dispatch(chatroom_message);// send off the message!! goodbye
//
//                Message onlineUsersInfo = new Message("Server", client, onlineCount.get(), new Date(), Type.ONLINE_USERS);
//                super.dispatch(onlineUsersInfo);
//              }

  // CHATROOM BROADCAST
//        ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online

//        for (String client : client_list) { // loop through users
////          Message chatroom_message = new Message("Server", client, this.identifier + " just left the server.", new Date(), Type.CHATROOM);
////          super.dispatch(chatroom_message); // send off the message!! goodbye
//
//          Message onlineUsersInfo = new Message("Server", client, onlineCount.get(), new Date(), Type.ONLINE_USERS);
//          super.dispatch(onlineUsersInfo);
//        }

  public void listen()  {

    Message input;
    while (true) {
      try {
        input = (Message) in.readObject();

        if (input.getType() == Type.USERNAME_PROPAGATE) {
          input.setSender(super.getIdentifier());
        }

        super.dispatch(input);

      } catch (ClassNotFoundException e) {
        System.out.println("Message data corrupted");
      }
      catch (IOException e ){

        // The stream has closed so just kick the user
        this.directory.remove(this.getIdentifier());
        super.decrementPublishOnlineCount();
        return;

      }
    }
  }

  public void send(Message message){

    // COMMENT OUT SYNCHRONIZED FOR AN ERROR
    synchronized (this.out) {

      try {
        this.out.writeObject(message);
        this.out.flush();
      } catch (IOException e) {
        // FIXME: This might need to be changed.
//        super.decrementPublishOnlineCount();
        this.directory.remove(this.getIdentifier());
      }

    }
  }
}
