import ClientDirectory.*;
import ConnectedClient.OnlineCountTestConnectedClient;
import ConnectedClient.ProdcuerConnectedClient;
import Message.Message;
import MessageConsumer.MessageConsumer;
import MessageQueue.MessageQueue;
import OnlineCount.*;

import java.util.ArrayList;

public class OnlineCountTest {


  public static void main(String[] args) throws InterruptedException {

    OnlineCount counter = new TSOnlineCount();
//    OnlineCount counter = new NonTSOnlineCount();

    ArrayList<Thread> list1 = new ArrayList<>();
    ArrayList<Thread> list2 = new ArrayList<>();

    Thread t1 = new Thread(() -> {
      for (int i = 0; i < 300; i ++ ) {
        OnlineCountTestConnectedClient c = new OnlineCountTestConnectedClient(counter);
        c.start();
        list1.add(c);
      }
    });

    Thread t2 = new Thread(() -> {
      for (int i = 0; i < 300; i ++ ) {
        OnlineCountTestConnectedClient c = new OnlineCountTestConnectedClient(counter);
        c.start();
        list2.add(c);
      }
    });

    t1.start();
    t2.start();

    t1.join();
    t2.join();

    System.out.println("All users logged in");
    System.out.println("Number of online users: " +  counter.get());

    System.out.println("Users will start loggin out");

    for (Thread t : list1) {
      t.join();
    }
    for (Thread t : list2) {
      t.join();
    }

    System.out.println("All users should be logged out");
    System.out.println("Number of online users: " +  counter.get());







  }
}
