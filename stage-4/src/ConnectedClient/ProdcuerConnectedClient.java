package ConnectedClient;

import Message.Message;
import MessageQueue.MessageQueue;

public class ProdcuerConnectedClient extends ConnectedClient {
  ProducerFunction producer;

  public ProdcuerConnectedClient(MessageQueue messageQueue, ProducerFunction producer) {
    super(messageQueue, null);
    this.producer = producer;
  }

  @Override
  public void listen() {
    producer.produce();
  }

  @Override
  public void send(Message message) {
    System.out.println(message);
  }
}
