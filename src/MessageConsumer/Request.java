package MessageConsumer;

import Message.*;

import java.net.InetAddress;

public class Request<T extends Message> {


  public T getMessage() {
    return message;
  }

  public Context getCtx() {
    return ctx;
  }

  private T message;
  private Context ctx;

  public Request(T message, Context ctx) {
    this.message = message;
    this.ctx = ctx;
  }
}
