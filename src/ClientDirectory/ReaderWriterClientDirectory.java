package ClientDirectory;

import ConnectedClient.ConnectedClient;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Semaphore;


public class ReaderWriterClientDirectory implements ClientDirectory {

  private final HashMap<String, ConnectedClient> directory;

  private final int SLOT_LIM = 1;

  private int readCount;

  private final Semaphore read;
  private final Semaphore wrt;


  public ReaderWriterClientDirectory() {
    this.directory = new HashMap<>();
    this.read = new Semaphore(SLOT_LIM,true);
    this.wrt = new Semaphore(SLOT_LIM, true);
  }

  //read
  public ConnectedClient get(String identifier) {
    try {
      read.acquire();
      readCount++;
      if(readCount == 1) {
        wrt.acquire();
      }
      read.release();
      ConnectedClient c = this.directory.get(identifier);
      read.acquire();
      readCount--;
      if(readCount == 0) {
        wrt.release();
      }
      read.release();
      return c;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

  //write
  public ConnectedClient add(String identifier, ConnectedClient client) throws IdentifierTakenException {
    try {
      wrt.acquire();

      if (this.directory.containsKey(identifier)) {
        wrt.release();
        throw new IdentifierTakenException(identifier);
      }

      ConnectedClient c = this.directory.put(identifier,client);



      wrt.release();
      return c;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  //write
  public ConnectedClient remove(String identifier) {
    try {
      wrt.acquire();
      ConnectedClient c = this.directory.remove(identifier);
      wrt.release();
      return c;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  //write
  public ConnectedClient update(String oldname, String newName) throws IdentifierTakenException, IdentifierNotFoundException {
    try {

      wrt.acquire();
      // get the client
      ConnectedClient client = this.directory.get(oldname);

      // check if there is actually a client with the old username
      if (client == null) {
        wrt.release();
        throw new IdentifierNotFoundException(oldname);
      }

      // check to see if the new username is not already in use, return null if is
      if (this.directory.get(newName) != null) {
        wrt.release();
        throw new IdentifierTakenException(newName);
      }
      // put the new username entry in
      this.directory.put(newName, client);
      // Delete the old username entry
      this.directory.remove(oldname);
      // return client for the thrill of it
      wrt.release();
      return client;
    }
    catch(InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Set keySet() {
    try {
      read.acquire();
      readCount++;
      if(readCount == 1) {
        wrt.acquire();
      }
      read.release();
      Set ks = directory.keySet();
      read.acquire();
      readCount--;
      if(readCount == 0) {
        wrt.release();
      }
      read.release();
      return ks;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  };


}
