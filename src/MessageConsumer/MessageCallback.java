package MessageConsumer;

import Message.*;

import java.util.List;

public interface MessageCallback<T extends Message> {
  List<? extends Message> execute(Request<T> request);
//  List<Message> execute(T message);
}