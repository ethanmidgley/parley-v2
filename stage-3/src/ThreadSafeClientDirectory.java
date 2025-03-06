import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeClientDirectory {

  private final ConcurrentHashMap<String, ConnectedClient> TSdirectory;

  private final Lock lock = new ReentrantLock();


  public ThreadSafeClientDirectory() {
    this.TSdirectory = new ConcurrentHashMap<>();
  }

  public ConnectedClient get(String identifier) {
    return this.TSdirectory.get(identifier);
  }

  public ConnectedClient add(String identifier, ConnectedClient client) {
    return this.TSdirectory.put(identifier, client);
  }

  public ConnectedClient remove(String identifier) {
    return this.TSdirectory.remove(identifier);
  }

  public ConnectedClient update(ConnectedClient identifier, String oldname, String newName){
    if (this.TSdirectory.contains(oldname)){
      return this.TSdirectory.put(newName, identifier);
    }else{
      return null;
    }
  }

  // function to update username
  // sets a global variable
  // while the global variable is set it pauses the listener
  // pulls in IP and current user
  // checks new user is not in use
  // sets user

  public void changeUsername(String oldUsername, String newUsername){
    lock.lock();
    try{
      if (!TSdirectory.containsKey(oldUsername) || TSdirectory.containsKey(newUsername)){
      //if directory doesn't contain current username or does contain new username then it can't continue
      }
      update(this.get(oldUsername),oldUsername,newUsername);
      
    }finally{
      lock.unlock();
    }
  }

}
