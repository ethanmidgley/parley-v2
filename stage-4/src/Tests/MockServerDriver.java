package Tests;

import ClientDirectory.ClientDirectory;
import ConnectedClient.ProdcuerConnectedClient;
import Message.*;
import MessageConsumer.MessageConsumer;
import MessageQueue.MessageQueue;
import ClientDirectory.ThreadSafeClientDirectory;
import MessageQueue.LinkedListMessageQueue;
import MessageQueue.TSLinkedListMessageQueue;

import java.util.ArrayList;
import java.util.Date;

public class MockServerDriver {

  private static final int NUMBER_CONSUMERS = 2;
  public static void main(String[] args) {

    ClientDirectory directory = new ThreadSafeClientDirectory();
    MessageQueue mq = new TSLinkedListMessageQueue();
//    MessageQueue mq = new LinkedListMessageQueue();
    MessageQueue logQ = new TSLinkedListMessageQueue();

    ArrayList<Thread> messageConsumers = new ArrayList<>();

    for (int i = 0; i < NUMBER_CONSUMERS; i++) {
      MessageConsumer mc = new MessageConsumer(directory, logQ, mq);
      Thread t = new Thread(mc);
      messageConsumers.add(t);
      t.start();
    }

    // Create two ProducerConnectedClients
    ProdcuerConnectedClient p1 = new ProdcuerConnectedClient(mq, () -> {
      for (int i = 0; i <10; i ++) {

        Message m = new Message("p1", "p2", String.valueOf(i), new Date(), Type.TEXT);
        mq.offer(m);
      }
    });

    ProdcuerConnectedClient p2 = new ProdcuerConnectedClient(mq, () -> {
      for (int i = 0; i <10; i ++) {
        Message m = new Message("p2", "p1", String.valueOf(i), new Date(), Type.TEXT);
        mq.offer(m);
      }
    });

    directory.add("p1", p1);
    directory.add("p2", p2);

    p1.start();
    p2.start();

  }
}
