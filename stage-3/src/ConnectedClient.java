public abstract class ConnectedClient extends Thread {
  private final MessageQueue mq;

  public ConnectedClient(MessageQueue mq){
    this.mq = mq;
  }
  abstract void listen();
  abstract void send(Message message);
  public void run() {
    this.listen();
  }

  public void dispatch(Message message) {
    this.mq.offer(message);
  }
}