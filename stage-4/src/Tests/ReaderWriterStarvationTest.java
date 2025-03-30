package Tests;

import ClientDirectory.ClientDirectory;
import ClientDirectory.ReaderWriterClientDirectory;
import ConnectedClient.ConnectedClient;
import ConnectedClient.ProdcuerConnectedClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;



// TEST CASE WE DON'T REALLY NEED BUT HAVE
public class ReaderWriterStarvationTest {

  public static void main(String[] args) {

    ClientDirectory directory;

    directory = new ReaderWriterClientDirectory();

    List<String> username = new ArrayList<String>();
    username.add("MUX");
    username.add("FRAZ");
    username.add("ETHAN");

    // Create two ProducerConnectedClients
    ProdcuerConnectedClient reader = new ProdcuerConnectedClient(null, () -> {

      int current = 1;


      for (;;) {
        ConnectedClient c = directory.get(username.get(current));
      }


    });

    ProdcuerConnectedClient writer = new ProdcuerConnectedClient(null, () -> {

      int current = 0;
      int next = 1;

      ExecutorService executorService = Executors.newSingleThreadExecutor();

      for (int i = 0; i < 1000; i++) {

        String current_username = username.get(current);
        String next_username = username.get(next);


        Future<ConnectedClient> future = executorService.submit(() -> {
          ConnectedClient c = directory.update(current_username, next_username);
          return c;
        });

        try {
          // Wait for at most 10 seconds until the result is returned
          ConnectedClient result = future.get(5, TimeUnit.SECONDS);
          System.out.println("Result: " + result);
        } catch (TimeoutException e) {
          System.out.println("No response from server!");
          System.exit(0);
        } catch (InterruptedException | ExecutionException e) {
          System.err.println("An error occurred: " + e.getMessage());
        }

        next = (next + 1) % 3;
        current = (current + 1) % 3;

      }

      executorService.shutdownNow();

    });


    directory.add("FRAZER", reader);
    directory.add("MUX", writer);
    System.out.println("Original number of clients: " + directory.keySet().size());

    reader.start();
    writer.start();

//    p1.join();
//    p2.join();

    System.out.println("Number of clients after username changing: " + directory.keySet());
    System.out.println("Number of clients after username changing: " + directory.keySet().size());


  }
}
