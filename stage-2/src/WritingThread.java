public class WritingThread extends Thread {
  private ClientDirectory directory;
  private String to;
  private String message;

  WritingThread (ClientDirectory directory, String to, String message) {
    this.directory = directory;
    this.to = to;
    this.message = message;
  }

  @Override
  public void run() {
    // Get the correct user to send it from the directory
    ConnectedClient client = directory.get(this.to);

    if (client == null) {
      // We are gonna have to do some handling here, maybe we send a message back to the person who tried to send one
      return;
    }

    client.send(this.message);


  }
}
