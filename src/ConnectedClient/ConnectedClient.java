package ConnectedClient;

import MessageQueue.MessageQueue;
import OnlineCount.OnlineCount;
import Message.Message;

import java.net.InetAddress;

public abstract class ConnectedClient extends Thread {

  private final MessageQueue mq;
  private String identifier;
  private InetAddress address;


  public OnlineCount onlineCount;


  // This is for virtual connected clients, such as chat rooms or maybe even games
  public ConnectedClient(String identifier, MessageQueue mq){
    this.identifier = identifier;
    this.mq = mq;

    this.address = null;
    this.onlineCount = null;
  }

  // This is for more physical clients in which we actually need the physical address
  public ConnectedClient(InetAddress address, MessageQueue mq, OnlineCount onlineCount){
    this.identifier = address.getHostAddress();
    this.address = address;
    this.mq = mq;
    this.onlineCount = onlineCount;
  }

  abstract public void listen();

  abstract public void send(Message message);

  public void run() {
    this.listen();
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public InetAddress getInetAddress() {
    return address;
  }

  public void setInetAddress(InetAddress address) {
    this.address = address;
  }


  public void dispatch(Message message) {
    this.mq.offer(message);
  }
}