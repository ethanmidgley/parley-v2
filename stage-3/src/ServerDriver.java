import java.io.IOException;
import java.util.ArrayList;

public class ServerDriver {
  private static final int NUMBER_CONSUMERS = 10;
  public static void main(String[] args) {

    ClientDirectory directory = new ThreadSafeClientDirectory();
    MessageQueue mq = new TSLinkedListMessageQueue();

    ArrayList<Thread> messageConsumers = new ArrayList<>();

    for (int i = 0; i < NUMBER_CONSUMERS; i++) {
      MessageConsumer mc = new MessageConsumer(directory, mq);
      Thread t = new Thread(mc);
      messageConsumers.add(t);
      t.start();
    }

    try {
      TCPConnectionListener tcp = new TCPConnectionListener(directory, mq, 8085);
      tcp.start();
    }
    catch (IOException e) {
      System.out.println("TCPListener failed - port may already be in use");
      return;
    }

  }
}
