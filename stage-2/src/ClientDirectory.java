import java.util.HashMap;

public class ClientDirectory {

  private HashMap<String, ConnectedClient> directory;

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

}
