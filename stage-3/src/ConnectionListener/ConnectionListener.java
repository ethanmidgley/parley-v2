package ConnectionListener;

public abstract class ConnectionListener extends Thread {
  abstract public void listen();
  public void run() {
    this.listen();
  }
}
