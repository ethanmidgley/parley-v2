interface ProducerFunction {
  void produce();
}

public class ProdcuerConnectedClient extends ConnectedClient {
  ProducerFunction producer;

  ProdcuerConnectedClient(MessageQueue messageQueue, ProducerFunction producer) {
    super(messageQueue);
    this.producer = producer;
  }

  @Override
  void listen() {
    producer.produce();
  }

  @Override
  void send(Message message) {
    System.out.println(message.toString());
  }
}
