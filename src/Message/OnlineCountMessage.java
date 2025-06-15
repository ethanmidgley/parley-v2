package Message;

public class OnlineCountMessage extends Message {

  private final String count;

  public OnlineCountMessage(String recipient, String count) {
    super( "Server", recipient, Type.ONLINE_USERS);
    this.count = count;
  }

  public String getCount() {
    return count;
  }

}