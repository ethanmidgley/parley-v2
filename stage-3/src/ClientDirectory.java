public interface ClientDirectory {
      
  public ConnectedClient get(String identifier);

  public ConnectedClient add(String identifier, ConnectedClient client);

  public ConnectedClient remove(String identifier);

  public ConnectedClient update(ConnectedClient identifier, String oldname, String newName);

  public void changeUsername(String oldUsername, String newUsername);

}
