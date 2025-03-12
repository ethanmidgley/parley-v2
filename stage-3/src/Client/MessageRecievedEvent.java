package Client;

import Message.Message;

public interface MessageRecievedEvent {
  void trigger(Message message);
}