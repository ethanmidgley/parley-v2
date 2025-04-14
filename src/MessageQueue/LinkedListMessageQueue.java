package MessageQueue;

import Message.Message;

import java.util.LinkedList;
import java.util.Queue;

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
