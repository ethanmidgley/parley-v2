package MessageQueue;

import Message.Message;

public interface MessageQueue {
  void offer(Message message);
  Message poll();
  boolean isEmpty();
}
