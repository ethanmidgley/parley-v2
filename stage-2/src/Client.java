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

    String server_ip = args[0];

    if (!isValidIPv4(server_ip)) {
      System.out.println("Invalid ip address");
      return;
    }

    final int LISTENING_PORT = 8008;
    final int WRITING_PORT = 8085; //808's :D

    // We create our listening server
    // 127.0.0.1:8008
    LocalServer server = new LocalServer(LISTENING_PORT);
    server.start();
    // Maybe a print so you can share address
    Socket writingSocket = null;

    String user_name = "usr";

  try {

    BufferedReader std_in = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Please enter your username - ");
    while (writingSocket == null) {
      String input = std_in.readLine();

      if (input.isEmpty()) {
        System.out.println("Invalid user name");
        continue;
      } else {
        user_name = input;
      }

//      try {
//        port = Integer.parseInt(input[1]);
//      } catch (NumberFormatException e) {
//        System.out.println("Invalid port number");
//        continue;
//      }


      try {
        writingSocket = new Socket(server_ip, WRITING_PORT);
      } catch (UnknownHostException e) {
        System.out.println("Unknown host");
      }
    }

    PrintWriter out = new PrintWriter(writingSocket.getOutputStream(), true);
    out.println(user_name);


    String input;
    while (true) {
      System.out.print("You: ");
      input = std_in.readLine();

      System.out.printf("\033[1A[2K\rYou: %s\n", input);

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
