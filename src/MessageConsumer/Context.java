package MessageConsumer;

import ClientDirectory.ClientDirectory;
import MessageQueue.MessageQueue;

public class Context {

  private final MessageQueue loggerQ;

  public MessageQueue getMessageQ() {
    return messageQ;
  }

  private final MessageQueue messageQ;

  public ClientDirectory getDirectory() {
    return directory;
  }

  public MessageQueue getLoggerQ() {
    return loggerQ;
  }

  private final ClientDirectory directory;


  public Context(ClientDirectory directory, MessageQueue logQ, MessageQueue messageQ) {
    this.directory = directory;
    this.loggerQ = logQ;
    this.messageQ = messageQ;
  }


}
