public abstract class ConnectedClient extends Thread {
  abstract void listen();
  abstract void send(String message);

  public void run() {
    this.listen();
  }
}