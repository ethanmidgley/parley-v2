package ConnectedClient;

import MessageQueue.MessageQueue;
import OnlineCount.OnlineCount;
import Message.Message;

public abstract class ConnectedClient extends Thread {
  private final MessageQueue mq;
  public OnlineCount onlineCount;

  public ConnectedClient(MessageQueue mq, OnlineCount onlineCount){
    this.mq = mq;
    this.onlineCount = onlineCount;
  }
  abstract public void listen();
  abstract public void send(Message message);
  public void run() {
    this.listen();
  }


  public void dispatch(Message message) {
    this.mq.offer(message);
  }
}