package ConnectedClient;

import ClientDirectory.ClientDirectory;
import MessageQueue.MessageQueue;
import OnlineCount.OnlineCount;
import Message.Message;
import Message.OnlineCountMessage;

import java.net.InetAddress;
import java.util.*;

public abstract class ConnectedClient extends Thread {

  private final MessageQueue mq;
  private String identifier;
  private InetAddress address;

  private Set<DependentClient> dependents;

  public boolean isVirtualClient() {
    return virtualClient;
  }

  private boolean virtualClient;

  public OnlineCount onlineCount;

  public void incrementPublishOnlineCount() {
    this.onlineCount.increment();
    publishOnlineCount();
  }

  public void decrementPublishOnlineCount() {
    this.onlineCount.decrement();
    publishOnlineCount();
  }


  public void publishOnlineCount() {
    OnlineCountMessage mes = new OnlineCountMessage("", onlineCount.get());
    mq.offer(mes);
  }

  // This is for virtual connected clients, basically games
  public ConnectedClient(String identifier, MessageQueue mq){
    this.identifier = identifier;
    this.mq = mq;
    this.virtualClient = true;
    this.dependents = new HashSet<>();
    this.address = null;
    this.onlineCount = null;
  }

  // This is for more physical clients in which we actually need the physical address
  public ConnectedClient(InetAddress address, MessageQueue mq, OnlineCount onlineCount){
    this.identifier = address.getHostAddress();
    this.address = address;
    this.virtualClient = false;
    this.dependents = new HashSet<>();
    this.mq = mq;
    this.onlineCount = onlineCount;
  }

  public void addDependent(DependentClient dependent) {
    this.dependents.add(dependent);
  }

  public void removeDependent(DependentClient dependent) {
    this.dependents.remove(dependent);
  }

  public void cascadeLeave() {
    this.dependents.forEach((dependentClient -> dependentClient.onDependencyLeave(this)));
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