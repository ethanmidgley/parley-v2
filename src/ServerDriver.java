import ClientDirectory.ClientDirectory;
import ConnectionListener.TCPConnectionListener;
import MessageConsumer.MessageConsumer;
import MessageQueue.MessageQueue;
import OnlineCount.OnlineCount;
import OnlineCount.TSOnlineCount;
import ClientDirectory.*;
import MessageQueue.*;
import ServerLogger.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ServerDriver {
  private static final int NUMBER_CONSUMERS = 10;

      public static void main(String[] args) {

        ClientDirectory directory = new ReaderWriterClientDirectory();
        MessageQueue mq = new TSLinkedListMessageQueue();
        MessageQueue logQ = new TSLinkedListMessageQueue();
        File log = new File("./log.txt");

        //start the logger up
        new Thread(new ThreadSafeLogger(logQ,log)).start();
        OnlineCount onlineCount = new TSOnlineCount();

        ArrayList<Thread> messageConsumers = new ArrayList<>();

        for (int i = 0; i < NUMBER_CONSUMERS; i++) {
          MessageConsumer mc = new MessageConsumer(directory, logQ, mq);
          Thread t = new Thread(mc);
          messageConsumers.add(t);
          t.start();
        }

        try {
          TCPConnectionListener tcp = new TCPConnectionListener(directory, mq, 8085, onlineCount);
          tcp.start();
        }
        catch (IOException e) {
          System.out.println("TCPListener failed - port may already be in use");
          return;
        }
      }
}
