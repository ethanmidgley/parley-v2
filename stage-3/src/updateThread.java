import java.util.Date;

public class updateThread extends Thread {
  private final ClientDirectory directory;
  private final Message message;
  updateThread (ClientDirectory directory, Message message) {
    this.directory = directory;
    this.message = message;
  }

  @Override
  public void run() {
    // Get the correct user to send it from the directory
    ConnectedClient client = directory.get(this.message.getRecipient());
    // System.out.println("I get to here");
    System.out.println(client);

    if (client == null) {
      Message error_message = new Message("Server", this.message.getSender(), "Recipient not found", new Date(), Type.SERVER);
      client = directory.get(this.message.getSender());
      client.send(error_message);
      return;
    }

    directory.changeUsername(this.message.getSender(), this.message.getContent());

  }
}