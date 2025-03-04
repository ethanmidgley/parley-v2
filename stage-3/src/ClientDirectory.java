import java.util.HashMap;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantLock;

public class ClientDirectory {

  private final HashMap<String, ConnectedClient> directory;

  private final Lock lock = new ReentrantLock();

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
  public boolean changeUsername(String oldUsername, String newUsername){
    lock.lock();
    try{
      if (!directory.containsKey(oldUsername) || directory.containsKey(newUsername)){
      return false; //if directory doesn't contain current username or does contain new username then it can't continue
      }
      ClientState client = directory.remove(oldUsername):
      client.setUsername(newUsername);
      directory.put(newUsername, client);
      return true;
    }finally{
      lock.unlock();
    }
    
    
    
    return true;
  }

}
