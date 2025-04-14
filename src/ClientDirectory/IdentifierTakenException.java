package ClientDirectory;

public class IdentifierTakenException extends RuntimeException {

  public IdentifierTakenException(String identifier) {
    super("Identifier already taken: " + identifier);
  }

}
