package Client;

import Message.Message;

public interface ReplyHandler<T extends Message> {
  void trigger(T message, MessageReceivedEvent<T> fallback);
}
