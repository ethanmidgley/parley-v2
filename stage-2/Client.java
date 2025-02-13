import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

  public static boolean isValidIPv4(String ip) {
    // Step 1: Separate the given string into an array of strings using the dot as delimiter
    String[] parts = ip.split("\\.");

    // Step 2: Check if there are exactly 4 parts
    if (parts.length != 4) {
      return false;
    }

    // Step 3: Check each part for valid number
    for (String part : parts) {
      try {
        // Step 4: Convert each part into a number
        int num = Integer.parseInt(part);

        // Step 5: Check whether the number lies in between 0 to 255
        if (num < 0 || num > 255) {
          return false;
        }
      } catch (NumberFormatException e) {
        // If parsing fails, it's not a valid number
        return false;
      }
    }

    // If all checks passed, return true
    return true;
  }

  public static void main(String[] args)  {

    int port_number = Integer.parseInt(args[0]);

    // We create our listening server
    LocalServer server = new LocalServer(port_number);
    server.start();
    // Maybe a print so you can share address
    Socket client = null;


    try {

    BufferedReader std_in = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Please enter client address and port (eg 192.168.1.3:6911) - ");
    String address = null;
    int port = -1;
    while (client == null) {
      String[] input = std_in.readLine().split(":");

      if (input.length != 2) {
        System.out.println("Invalid server address");
        continue;
      }

      if (!isValidIPv4(input[0])) {
        System.out.println("Invalid ip address");
        continue;
      } else {
        address = input[0];
      }

      try {
        port = Integer.parseInt(input[1]);
      } catch (NumberFormatException e) {
        System.out.println("Invalid port number");
        continue;
      }


      try {
        client = new Socket(address, port);
      } catch (UnknownHostException e) {
        System.out.println("Unknown host");

      }
    }

    PrintWriter out = new PrintWriter(client.getOutputStream(), true);


    String input;
    while (true) {
      System.out.print("You: ");
      input = std_in.readLine();
      if (input.equals("exit")) {
        break;
      }
      // Send back the data
      out.println(input);
    }

    } catch (IOException e) {
      System.out.println("Buffer closed.");
      System.exit(0);
    }


  }
}
