package Chatroom;

import ConnectedClient.ConnectedClient;
import Message.Message;
import MessageQueue.MessageQueue;
import OnlineCount.OnlineCount;

import java.util.ArrayList;

public class Chatroom extends ConnectedClient {

  private final ArrayList<ConnectedClient> clients ;

  public Chatroom (String identifier, MessageQueue mq) {
    super(identifier, mq, null);
    this.clients = new ArrayList<>();
  }

  public void listen() {

  }

  public void send(Message message) {

    // If we send a message to the chat room we want to send that message to all chatroom participants
    for (ConnectedClient client : clients) {
      if (!client.getIdentifier().equals(message.getSender())) {
        super.dispatch(message);
      }
    }

  }
}
