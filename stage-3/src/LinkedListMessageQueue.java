import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedListMessageQueue implements MessageQueue {
  private final Queue<Message> messageQueue;

  public LinkedListMessageQueue() {
    this.messageQueue = new LinkedList<>();
  }

  public void offer(Message m) {
    this.messageQueue.offer(m);
  }

  public Message poll() {
    return messageQueue.poll();
  }

  public boolean isEmpty() {
    return messageQueue.isEmpty();
  }

}
