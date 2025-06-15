package Message;

import java.util.UUID;

public class TextMessage extends Message {

  private final String content;

  public TextMessage(String sender, String recipient, String content, Type type) {
    super(sender, recipient, type);
    this.content = content;
  }

  public TextMessage(UUID identifier, String sender, String recipient, String content, Type type) {
    super(identifier, sender, recipient, type);
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public TextMessage reply(String content) {
    return new TextMessage(this.getId(), "Server", this.getSender(), content, Type.SERVER);
  }
}
