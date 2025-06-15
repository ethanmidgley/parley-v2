package Message;

import java.util.UUID;

public class UsernameMessage extends Message {


  private  String username;

  public UsernameMessage(String sender, String username, boolean update) {
    super(sender, "", update ? Type.USERNAME_UPDATE : Type.USERNAME_PROPAGATE);
    this.username = username;
  }

  public UsernameMessage(UUID identifier, String sender, String username, boolean update) {
    super(identifier, sender, "", update ? Type.USERNAME_UPDATE : Type.USERNAME_PROPAGATE);
    this.username = username;
  }

  public String getUsername() {
    return this.username;
  }


  @Override
  public Message successReply(String message) {
    return new SuccessMessage(super.getId(), this.username, message);
  }


}
