public interface MessageQueue {
  public void push(Message message);
  public Message pop();

}
