import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedListMessageQueue implements MessageQueue{
  private final Queue<Message> messageQueue;
  private final Lock lock;
//  private final Condition isOnline;

  public LinkedListMessageQueue() {
    this.messageQueue = new LinkedList<>();
    this.lock = new ReentrantLock();
  }

  public void push(Message m) {
    lock.lock();
    this.messageQueue.add(m);
    lock.unlock();
  }

  public Message pop() {
    lock.lock();
    Message m = messageQueue.poll();
    lock.unlock();
    return m;
  }
}
