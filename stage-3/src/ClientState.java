import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientState {
  private String current_conversation;
  private String username;

  private final Map<String, List<Message>> messages;

  public ClientState() {
    this.current_conversation = null;
    this.messages = new HashMap<>();
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getCurrentConversation() {
    return this.current_conversation;
  }

  public List<Message> getMessages(String conversation) {
    return this.messages.get(conversation);
  }

  public void setCurrentConversation(String current_conversation) {
    this.current_conversation = current_conversation;
  }

  public void addMessageBySender(Message message) {
    this.messages.get(message.getSender()).add(message);
  }

  public void addMessageByRecipient(Message message) {
    this.messages.get(message.getRecipient()).add(message);
  }

}
