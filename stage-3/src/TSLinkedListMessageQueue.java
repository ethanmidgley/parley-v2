import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TSLinkedListMessageQueue implements MessageQueue{
  private final Queue<Message> messageQueue;
  private final Lock headLock;
  private final Lock tailLock;
//  private final Condition isOnline;

  public TSLinkedListMessageQueue() {
    this.messageQueue = new LinkedList<>();
    this.headLock = new ReentrantLock();
    this.tailLock = new ReentrantLock();
  }

  public void offer(Message m) {
    tailLock.lock();
    this.messageQueue.offer(m);
    tailLock.unlock();
  }

  public Message poll() {
    headLock.lock();
    try {
      return messageQueue.poll();
    }
    finally {
      headLock.unlock();
    }
  }

  public boolean isEmpty() {
    headLock.lock();
    try {
      return messageQueue.isEmpty();
    }
    finally {
      headLock.unlock();
    }
  }
}
