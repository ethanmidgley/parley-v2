import java.io.*;

public abstract class ConnectedClient extends Thread {
  private ObjectInputStream in;
  private ObjectOutputStream out;
  abstract void listen();
  abstract void send(Message message);

  public void run() {
    this.listen();
  }
}