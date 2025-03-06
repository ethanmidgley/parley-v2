import java.util.Date;

public class WritingThread extends Thread {
  private final ThreadSafeClientDirectory directory;
  private final Message message;
  WritingThread (ThreadSafeClientDirectory directory, Message message) {
    this.directory = directory;
    this.message = message;
  }

  @Override
  public void run() {
    // Get the correct user to send it from the directory
    ConnectedClient client = directory.get(this.message.getRecipient());

    if (client == null) {
      Message error_message = new Message("Server", this.message.getSender(), "Recipient not found", new Date(), Type.SERVER);
      client = directory.get(this.message.getSender());
      client.send(error_message);
      return;
    }

    client.send(this.message);


  }
}
