package Client;

import Message.Message;

public interface MessageReceivedEvent <T extends Message>{
  void trigger(T message);
}