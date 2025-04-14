package ClientDirectory;

public class IdentifierNotFoundException extends RuntimeException {
  public IdentifierNotFoundException(String identifier) {
    super("Identifier not found: " + identifier);
  }
}
