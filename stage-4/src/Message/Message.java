package Message;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
  private final String sender;
  private final String recipient;
  private final String content;
  private final Date sendDate;
  private Type type;

  public Message(String sender, String recipient, String content, Date sendDate, Type type) {
    this.sender = sender;
    this.recipient = recipient;
    this.content = content;
    this.sendDate = sendDate;
    this.type = type;
  }

  public String getSender() {
    return sender;
  }

  public String getRecipient() {
    return recipient;
  }

  public String getContent() {
    return content;
  }

  public Date getSendDate() {
    return sendDate;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String toString() {
    return "Sender: " + this.getSender() +
            " Recipient: " + this.getRecipient() +
            " Content: " + this.getContent() +
            " Date: " + this.getSendDate() +
            " Message.Type: " + this.getType().toString();
  }
}
