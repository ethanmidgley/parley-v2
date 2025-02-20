import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable{
  private String sender;
  private String recipient;

  private String content;

  private Date sendDate;

  public Message(String sender, String recipient, String content, Date sendDate) {
    this.sender = sender;
    this.recipient = recipient;
    this.content = content;
    this.sendDate = sendDate;
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
}
