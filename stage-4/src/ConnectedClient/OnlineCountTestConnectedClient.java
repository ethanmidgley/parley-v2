package ConnectedClient;

import Message.Message;
import OnlineCount.OnlineCount;

public class OnlineCountTestConnectedClient extends ConnectedClient{

  public OnlineCountTestConnectedClient(OnlineCount counter) {
    super(null, counter);
    onlineCount.increment();
  }

  public void listen() {
    try {
      Thread.sleep(5000);
      onlineCount.decrement();
    }  catch (InterruptedException e) {
      System.out.println("Man shut the fuck up jvm");
    }
  }

  public void send(Message message) {
//    System.out.println(message.toString());
  }

}
