import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueueDirectory {
  Queue<MessageQueue> dispatchQueue;
  Map<String,MessageQueue> messageDirectory;

  public MessageQueueDirectory(ClientDirectory cl) {
    this.messageDirectory = new HashMap<>();
    this.dispatchQueue = new ConcurrentLinkedQueue<>();
  }

  public void addMessage(Message m) {
    String to = m.getRecipient();
    if(messageDirectory.containsKey(to)) {
      // only push to queue within the directory as the reference will be updated in dispatchQueue
      messageDirectory.get(to).push(m);
    }
    else {
      // if no previous conversation existed between the two parties then establish a Messages Queue conversation
      // FIXME: this is not threadsafe might need locks
      MessageQueue mq = new LinkedListMessageQueue();
      mq.push(m);
      messageDirectory.put(to,mq);
      dispatchQueue.add(mq);
    }
  }

  public MessageQueue pop() {
    return dispatchQueue.poll();
  }
}
