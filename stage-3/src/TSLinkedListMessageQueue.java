import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TSLinkedListMessageQueue implements MessageQueue {
  private final Queue<Message> messageQueue;
  private final Lock headLock;
  private final Lock tailLock;

  private final Condition isNotEmpty;

//  private final Condition isOnline;

  public TSLinkedListMessageQueue() {
    this.messageQueue = new LinkedList<>();
    this.headLock = new ReentrantLock();
    this.tailLock = new ReentrantLock();
    this.isNotEmpty = headLock.newCondition();
  }

  public void offer(Message m) {
    tailLock.lock();
    try {
      this.messageQueue.offer(m);
      this.isNotEmpty.signal();
    } finally {
      tailLock.unlock();
    }
  }

  public Message poll() {
    headLock.lock();
    try {

      while (this.messageQueue.isEmpty()) {
        try {
          isNotEmpty.await();
        } catch (InterruptedException e) {
        }
      }
      return messageQueue.poll();
    } finally {
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
