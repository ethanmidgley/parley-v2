package ClientDirectory;

import java.util.HashMap;
import java.util.Set;
import ConnectedClient.ConnectedClient;


public class HashmapClientDirectory implements ClientDirectory {

  private final HashMap<String, ConnectedClient> directory;

  public HashmapClientDirectory() {
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

  public ConnectedClient update(String oldname, String newName){

    // get the client
    ConnectedClient client = this.get(oldname);

    // check if there is actually a client with the old username
    if (client == null) {
      return null;
    }

    // check to see if the new username is not already in use, return null if is
    if (this.directory.get(newName) != null){
      return null;
    }
    // put the new username entry in
    this.directory.put(newName, client);
    // Delete the old username entry
    this.directory.remove(oldname);
    // return client for the thrill of it
    return client;
  }

  public Set keySet() {
    return this.directory.keySet();
  };


}
