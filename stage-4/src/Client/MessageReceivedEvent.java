package Client;

import Message.Message;

public interface MessageReceivedEvent {
  void trigger(Message message);
}