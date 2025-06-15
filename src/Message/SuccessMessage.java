package Message;

import java.util.UUID;

public class SuccessMessage extends Message {



  private final String message;

  public SuccessMessage(String recipient, String message) {
    super( "Server", recipient, Type.SUCCESS);
    this.message = message;
  }


  public SuccessMessage(UUID identifier, String recipient, String message) {
    super(identifier, "Server", recipient, Type.SUCCESS);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

}