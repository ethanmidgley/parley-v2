package MessageConsumer;
import ClientDirectory.ClientDirectory;
import ConnectedClient.ConnectedClient;
import Message.*;
import MessageQueue.MessageQueue;
import java.util.ArrayList;
import java.util.Date;

public class MessageConsumer implements Runnable{
  private final MessageQueue mq;
  private final ClientDirectory directory;

  public MessageConsumer(ClientDirectory directory, MessageQueue mq) {
    this.directory = directory;
    this.mq = mq;
  }

  private void send_message(Message m) {

    ConnectedClient client = this.directory.get(m.getRecipient());

    if (client == null) {
      Message error_message = new Message("Server", m.getSender(), "Recipient not found", new Date(), Type.SERVER);
      client = directory.get(m.getSender());
      client.send(error_message);
      return;
    }

    client.send(m);
  }

  private void consume() {

    for (;;) {

      Message m = this.mq.poll();

      if (m == null) {
        continue;
      }



      switch(m.getType()){

        case USERNAME_PROPAGATE -> { // this is the case where the user is setting up their username to their ip
          if (this.directory.get(m.getContent()) == null) { // check if username doesn't already exist

            ConnectedClient c = this.directory.update(m.getSender(), m.getContent());
            c.setIdentifier(m.getContent());

//              ServerDriver.UpdateOnlineUsers("+");

            ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online

            for (String client : client_list) { // loop through users
              Message chatroom_message = new Message("Server", client, m.getSender() + " just joined the server!", new Date(), Type.CHATROOM);
              send_message(chatroom_message);// send off the message!! goodbye

              //TODO: STOP COUPLING LIKE THIS FRAZ
//                Message onlineUsersInfo = new Message("Server", client, ServerDriver.getNumOnline(), new Date(), Type.ONLINE_USERS);
//                super.dispatch(onlineUsersInfo);
            }

          } else {
            ConnectedClient c = this.directory.get(m.getSender());
            Message error_message = new Message("Server",
                    c.getIdentifier(),
                    "Error - Name already taken",
                    new Date(),
                    Type.SERVER);
            send_message(error_message);
          }
        }

        case TEXT -> { // this is the case for a regular message
          send_message(m);
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
            Message chatroom_message = new Message(m.getSender(), client, m.getContent(), m.getSendDate(), Type.CHATROOM); // create a new message with chatroom enum

            if (!(chatroom_message.getRecipient().equals(chatroom_message.getSender()))){ // so we dont send a message back to ourselves
              send_message(chatroom_message); // send off the message!! goodbye
            }
          }
        }

        case UPDATE_USERNAME -> { // this is the case to update username of a user


          ConnectedClient c = directory.update(m.getSender(), m.getContent());
          if (c == null) {
            Message error_message = new Message("Server",
                    c.getIdentifier(),
                    "Error - Could not change username, try a different username",
                    new Date(),
                    Type.SERVER);
            send_message(error_message);
          } else {

            c.setIdentifier(m.getContent());
            Message success_message = new Message("Server", c.getIdentifier(), c.getIdentifier(), new Date(), Type.UPDATE_USERNAME);
            send_message(success_message);

          }
        }

      }


    }
  }



  @Override
  public void run() {
    consume();
  }
}
