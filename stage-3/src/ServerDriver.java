import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.*;

public class ServerDriver {
  private static final int NUMBER_CONSUMERS = 10;
  private static int onlineUsers = 0;
    static Lock lock = new ReentrantLock();
      public static void main(String[] args) {
    
        ClientDirectory directory = new ClientDirectory();
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
    
      public static void UpdateOnlineUsers(String function){
        lock.lock();
        try{
          if (function.equals("+")){
            onlineUsers++;
          }
          else if (function.equals("-")){
            onlineUsers--;
          }
        }
        finally{
          lock.unlock();
        }
        
        //NON THREADSAFE VERSION
        //if (function.equals("+")){
        //  onlineUsers++;
        //}
        //else if (function.equals("-")){
        //  onlineUsers--;
        //}
        //System.out.println(onlineUsers);
      }

      public static String getNumOnline(){
        return String.valueOf(onlineUsers);
      }
}
