package ClientDirectory;

import java.util.Set;
import ConnectedClient.ConnectedClient;

public interface ClientDirectory {
      
  ConnectedClient get(String identifier);

  ConnectedClient add(String identifier, ConnectedClient client);

  ConnectedClient remove(String identifier);

  ConnectedClient update(String oldname, String newName);

  Set keySet();

}