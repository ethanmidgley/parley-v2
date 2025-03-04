import java.util.HashMap;
import java.util.ArrayList;

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

  public ArrayList<ConnectedClient> values() { return (ArrayList<ConnectedClient>) this.directory.values(); }
}
