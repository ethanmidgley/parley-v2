package ConnectedClient;

import MessageQueue.MessageQueue;
import Message.Message;

public abstract class ConnectedClient extends Thread {
  private final MessageQueue mq;
  private String identifier;

  public ConnectedClient(MessageQueue mq){
    this.mq = mq;
  }
  abstract public void listen();
  abstract public void send(Message message);
  public void run() {
    this.listen();
  }

  public void setIdentifier(String identifier) {this.identifier = identifier;};
  public String getIdentifier() {return this.identifier;};

  public void dispatch(Message message) {
    this.mq.offer(message);
  }
}