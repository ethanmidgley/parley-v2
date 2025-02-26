import java.io.Serializable;
import java.util.Date;

enum Type {
  USERNAME_PROPAGATE,
  TEXT,
  SIGNAL,
  SERVER
}

public class Message implements Serializable{
  private String sender;
  private String recipient;
  private String content;
  private Date sendDate;
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

  public Type getType() { return type; }
}
