package Tests;

import ClientDirectory.ClientDirectory;
import ClientDirectory.ThreadSafeClientDirectory;
import ConnectedClient.ProdcuerConnectedClient;
import Message.Message;
import Message.Type;
import MessageConsumer.MessageConsumer;
import MessageQueue.MessageQueue;
import MessageQueue.TSLinkedListMessageQueue;
import ServerLogger.ThreadSafeLogger;
import ServerLogger.ThreadUnsafeLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class LogTestEnv {

  private static final int NUMBER_CONSUMERS = 2;
  public static void main(String[] args) {

    ClientDirectory directory = new ThreadSafeClientDirectory();
//    MessageQueue.MessageQueue mq = new MessageQueue.MessageQueue.TSLinkedListMessageQueue();
    MessageQueue mq = new TSLinkedListMessageQueue();


    MessageQueue logQ = new TSLinkedListMessageQueue();
    File log = new File("./log.txt");

    //start the logger up
//    new Thread(new ThreadUnsafeLogger(logQ,log, 5)).start();
    new Thread(new ThreadSafeLogger(logQ,log, 5)).start();


    ArrayList<Thread> messageConsumers = new ArrayList<>();

    for (int i = 0; i < NUMBER_CONSUMERS; i++) {
      MessageConsumer mc = new MessageConsumer(directory, logQ, mq);
      Thread t = new Thread(mc);
      messageConsumers.add(t);
      t.start();
    }

    // Create two ProducerConnectedClients
    ProdcuerConnectedClient p1 = new ProdcuerConnectedClient(mq, () -> {
      for (int i = 0; i < 1000; i ++) {

        //NOTE: to test this shit we got to
        Message m = new Message("p1", "p1", String.valueOf(i), new Date(), Type.TEXT);
        mq.offer(m);
        try {
          Thread.sleep(10);
        } catch(InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    directory.add("p1", p1);

    p1.start();

  }
}
