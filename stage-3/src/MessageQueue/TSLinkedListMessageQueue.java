package MessageQueue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import Message.Message;

public class TSLinkedListMessageQueue implements MessageQueue {
  private final Queue<Message> messageQueue;
  private final Lock queueLock;
  private final Condition isNotEmpty;

  public TSLinkedListMessageQueue() {
    this.messageQueue = new LinkedList<>();
    this.queueLock = new ReentrantLock();
    this.isNotEmpty = queueLock.newCondition();
  }

  public void offer(Message m) {
    queueLock.lock();
    try {
      this.messageQueue.offer(m);
      this.isNotEmpty.signal();
    } finally {
      queueLock.unlock();
    }
  }

  public Message poll() {
    queueLock.lock();

    try {

      while (this.messageQueue.isEmpty()) {
        try {
          isNotEmpty.await();
        } catch (InterruptedException e) {
        }
      }
      return messageQueue.poll();
    } finally {
      queueLock.unlock();
    }
  }

  public boolean isEmpty() {
    queueLock.lock();
    try {
      return messageQueue.isEmpty();
    }
    finally {
      queueLock.unlock();
    }
  }


}
