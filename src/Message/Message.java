package Message;

import javafx.util.Pair;

import java.io.PipedInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

public class Message implements Serializable {
  private final UUID id;
  private String sender;
  private final String recipient;
  private final Date sendDate;
  private Type type;


  public Message(String sender, String recipient, Type type) {
    this.id = UUID.randomUUID();
    this.sender = sender;
    this.recipient = recipient;
    this.sendDate = new Date();
    this.type = type;
  }

  public Message(UUID identifier,String sender, String recipient, Type type) {
    this.id = identifier;
    this.sender = sender;
    this.recipient = recipient;
    this.sendDate = new Date();
    this.type = type;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

//  public void setRecipient(String recipient) {
//    this.recipient = recipient;
//  }


  public UUID getId() {
    return id;
  }

  public String getSender() {
    return sender;
  }

  public String getRecipient() {
    return recipient;
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
            " Date: " + this.getSendDate() +
            " Message.Type: " + this.getType().toString();
  }

  public Message reply() {
    return new Message(this.id, "Server", this.sender, Type.SERVER);
  }

  public Message errorReply(String message) {
    return new ErrorMessage(this.id, this.sender, message);
  }

  public Message successReply(String message) {
    return new SuccessMessage(this.id, this.sender, message);
  }



}
