import java.util.HashMap;


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

  public ConnectedClient update(ConnectedClient identifier, String oldname, String newName){
    if (this.directory.containsKey(oldname)){
      return this.directory.put(newName, identifier);
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

    if (!directory.containsKey(oldUsername) || directory.containsKey(newUsername)){
      //if directory doesn't contain current username or does contain new username then it can't continue
    }
      System.out.println("i get here");
      update(this.get(oldUsername),oldUsername,newUsername);
  }

}
