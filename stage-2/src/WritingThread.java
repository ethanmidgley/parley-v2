public class WritingThread extends Thread {
  private ClientDirectory directory;

  private Message message;
  WritingThread (ClientDirectory directory, Message message) {
    this.directory = directory;
    this.message = message;
  }

  @Override
  public void run() {
    // Get the correct user to send it from the directory
    ConnectedClient client = directory.get(this.message.getRecipient());

    if (client == null) {
      // We are gonna have to do some handling here, maybe we send a message back to the person who tried to send one
      return;
    }

    client.send(this.message);


  }
}
