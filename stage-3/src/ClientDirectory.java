import java.util.HashMap;

public class ClientDirectory {

  private final HashMap<String, ConnectedClient> directory;

  public ClientDirectory() {
    this.directory = new HashMap<>();
  }

  public ConnectedClient get(String identifier) {
    return this.directory.get(identifier);
  }

  public ConnectedClient add(String identifier, ConnectedClient client) {
    return this.directory.put(identifier, client);
  }

  public ConnectedClient remove(String identifier) {
    return this.directory.remove(identifier);
  }

  // function to update username
  // sets a global variable
  // while the global variable is set it pauses the listener
  // pulls in IP and current user
  // checks new user is not in use
  // sets user

}
