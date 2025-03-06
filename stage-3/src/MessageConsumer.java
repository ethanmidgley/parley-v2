import java.util.Date;

public class MessageConsumer implements Runnable{
  private final MessageQueue mq;
  private final ClientDirectory directory;
  public MessageConsumer(ClientDirectory directory, MessageQueue mq) {
    this.directory = directory;
    this.mq = mq;
  }

  private void consume() {
    for (;;) {

      Message m = this.mq.poll();
      ConnectedClient client = directory.get(m.getRecipient());

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
