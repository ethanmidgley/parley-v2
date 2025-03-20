package ConnectedClient;

import ClientDirectory.ClientDirectory;
import Message.Message;
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
  private String identifier;
  private final ClientDirectory directory;
  private static final int WRITING_PORT = 8008;


  public TCPConnectedClient(Socket socket, ClientDirectory directory, MessageQueue mq, OnlineCount onlineCount) throws IOException {
    super(mq,onlineCount);

    this.directory = directory;

    this.reading_socket = socket;
    this.in = new ObjectInputStream(reading_socket.getInputStream());

    this.writing_socket = new Socket(socket.getInetAddress().getHostAddress(), WRITING_PORT);
    this.out = new ObjectOutputStream(writing_socket.getOutputStream());

    // Client.Client created now let's add it to the directory with just the ip address as their name at the moment
    this.identifier = socket.getInetAddress().getHostAddress();
    this.directory.add(identifier, this);
    onlineCount.increment();

  }

  public void listen()  {

    Message input;
    while (true) {
      try {
        input = (Message) in.readObject();

        switch(input.getType()){

          case USERNAME_PROPAGATE -> { // this is the case where the user is setting up their username to their ip
            if (this.directory.get(input.getContent()) == null) { // check if username doesn't already exist
  
              this.directory.remove(this.identifier);
              this.directory.add(input.getContent(), this);
              this.identifier = input.getContent();
  
              ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online
  
              for (String client : client_list) { // loop through users
                Message chatroom_message = new Message("Server", client, input.getSender() + " just joined the server!", new Date(), Type.CHATROOM);
                super.dispatch(chatroom_message);// send off the message!! goodbye
  
                Message onlineUsersInfo = new Message("Server", client, onlineCount.get(), new Date(), Type.ONLINE_USERS);
                super.dispatch(onlineUsersInfo);
              }
  
            } else {
              Message error_message = new Message("Server",
                      this.identifier,
                      "Error - Name already taken",
                      new Date(),
                      Type.SERVER);
                      super.dispatch(error_message);
            }
          }
  
          case TEXT -> { // this is the case for a regular message
            super.dispatch(input);
          }
  
          case SIGNAL -> { // this is the case for the start of the handshake between users
            super.dispatch(input);
          }

          case SIGNAL_ACK -> { // this is the case for returning the handshake
            System.out.println(input);
            if (!(input.getContent().equals("Denied"))){
              String[] arr = input.getContent().split(":");
              Message success_message = new Message(input.getSender(), input.getRecipient(), reading_socket.getInetAddress().getHostAddress() + ":" + arr[1], new Date(), Type.SIGNAL_ACK);
              super.dispatch(success_message);

            } else {
              Message denial_message = new Message(input.getSender(), input.getRecipient(), "Denied", new Date(), Type.SIGNAL_ACK);
              super.dispatch(denial_message);
            }
          }
  
          case SERVER -> { // this is the case for a server message
            System.out.println("Error - User should not be able to send server messages");
          }
  
          case CHATROOM -> { // in the case of a message to a chatroom
            ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online
  
            for (String client : client_list){ // loop through users
              Message chatroom_message = new Message(input.getSender(), client, input.getContent(), input.getSendDate(), Type.CHATROOM); // create a new message with chatroom enum
              if (!(chatroom_message.getRecipient().equals(chatroom_message.getSender()))){ // so we dont send a message back to ourselves
                super.dispatch(chatroom_message); // send off the message!! goodbye
              }
            }
          }
  
          case UPDATE_USERNAME -> { // this is the case to update username of a user
            ConnectedClient c = directory.update(input.getSender(), input.getContent());
            if (c == null) {
              Message error_message = new Message("Server",
                      this.identifier,
                      "Error - Could not change username, try a different username",
                      new Date(),
                      Type.SERVER);
                      super.dispatch(error_message);
            } else {
              this.identifier = (input.getContent());
              Message success_message = new Message("Server", this.identifier, this.identifier, new Date(), Type.UPDATE_USERNAME);
              super.dispatch(success_message);
            }
          }
        }

      } catch (ClassNotFoundException e) {
        // TODO: Split in to two exceptions, notes on notion
        System.out.println("Message.Message data corrupted");
      }
      catch (IOException e ){
        // The stream has closed so just kick the user
        this.directory.remove(this.identifier);
        onlineCount.decrement();
        ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online

        for (String client : client_list) { // loop through users
          Message chatroom_message = new Message("Server", client, this.identifier + " just left the server.", new Date(), Type.CHATROOM);
          super.dispatch(chatroom_message); // send off the message!! goodbye

          Message onlineUsersInfo = new Message("Server", client, onlineCount.get(), new Date(), Type.ONLINE_USERS);
          super.dispatch(onlineUsersInfo);
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
