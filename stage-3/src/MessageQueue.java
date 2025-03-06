public interface MessageQueue {
  public void offer(Message message);
  public Message poll();

}
