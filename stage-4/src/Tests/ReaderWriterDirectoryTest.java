package Tests;

import ClientDirectory.ClientDirectory;
import ClientDirectory.ReaderWriterClientDirectory;
import ConnectedClient.ConnectedClient;
import ConnectedClient.ProdcuerConnectedClient;

import java.util.ArrayList;
import java.util.List;

public class ReaderWriterDirectoryTest {


  public static void main(String[] args) throws InterruptedException {

    ClientDirectory directory;

//    directory = new ThreadSafeClientDirectory();
//    directory = new HashmapClientDirectory();
    directory = new ReaderWriterClientDirectory();

    List<String> username = new ArrayList<String>();
    username.add("MUX");
    username.add("FRAZ");
    username.add("ETHAN");

    // Create two ProducerConnectedClients
    ProdcuerConnectedClient p1 = new ProdcuerConnectedClient(null, () -> {

      int current = 0;
      int next = 1;

      for (int i = 0; i <1000; i ++) {
        String current_username = username.get(current);
        String next_username = username.get(next);


        ConnectedClient c = directory.update(current_username, next_username);

        next = (next+1) % 3;
        current = (current+1) % 3;

      }
    });


    ProdcuerConnectedClient p2 = new ProdcuerConnectedClient(null, () -> {

      int current = 1;
      int next = 2;

      for (int i = 0; i <1000; i ++) {
        String current_username = username.get(current);
        String next_username = username.get(next);


        ConnectedClient c = directory.update(current_username, next_username);

        next = (next+1) % 3;
        current = (current+1) % 3;

      }
    });


    directory.add("MUX", p1);
    directory.add("FRAZ", p2);
    System.out.println("Original number of clients: " +  directory.keySet().size());

    p1.start();
    p2.start();

    p1.join();
    p2.join();

    System.out.println("Number of clients after username changing: " + directory.keySet());
    System.out.println("Number of clients after username changing: " + directory.keySet().size());


  }

}
