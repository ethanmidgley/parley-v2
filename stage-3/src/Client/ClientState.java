package Client;

import Message.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientState {
  private String current_conversation;
  private String username;

  private final Map<String, List<Message>> messages;

  public ClientState() {
    this.current_conversation = "";
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

  public void initialiseConversation(String conversation) {
    this.messages.put(conversation, new ArrayList<>());
  }

  public void addMessageBySender(Message message) {
    this.messages.get(message.getSender()).add(message);
  }

  public void addMessageByRecipient(Message message) {
    this.messages.get(message.getRecipient()).add(message);
  }

  public void addMessagesToChatroom(Message message) {
    this.messages.get("Chatroom").add(message);
  }
}
