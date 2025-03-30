package MessageConsumer;
import ClientDirectory.ClientDirectory;
import ConnectedClient.ConnectedClient;
import Message.*;
import MessageQueue.MessageQueue;
import java.util.Date;

public class MessageConsumer implements Runnable{
  private final MessageQueue mq;
  private final MessageQueue loggerQ;
  private final ClientDirectory directory;

  public MessageConsumer(ClientDirectory directory, MessageQueue logQ, MessageQueue mq) {
    this.directory = directory;
    this.loggerQ = logQ;
    this.mq = mq;
  }


  private void consume() {
    for (;;) {
      Message m = this.mq.poll();

      if (m == null) {
        continue;
      }

      this.loggerQ.offer(m);

      ConnectedClient client = this.directory.get(m.getRecipient());

      if (client == null) {
        Message error_message = new Message("Server", m.getSender(), "Recipient not found", new Date(), Type.SERVER);
        client = directory.get(m.getSender());
        client.send(error_message);
        return;
      }
  
      client.send(m); 
    }
  }


  @Override
  public void run() {
    consume();
  }
}
