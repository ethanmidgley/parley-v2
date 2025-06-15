package Message;

import java.util.UUID;

public class ErrorMessage extends Message {


  private final String message;

  public ErrorMessage(String recipient, String message) {
    super( "Server", recipient, Type.ERROR);
    this.message = message;
  }


  public ErrorMessage(UUID identifier,String recipient, String message) {
    super(identifier, "Server", recipient, Type.ERROR);
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }

}
