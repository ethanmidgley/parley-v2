public abstract class ConnectedClient extends Thread {
  abstract void listen();
  abstract void send(Message message);
  public void run() {
    this.listen();
  }
}