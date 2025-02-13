import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server{
  private int global_port = 6911;
  private String username;
  private InetAddress ip;
  HashMap<String, InetAddress> ipMap = new HashMap<>();

  public Server(int port) {
    this.global_port = port;
  }

  public void running(){
    while (true){
      // if it gets a connection
      // request();
    }

  }

  public void request(){
    try {
      ServerSocket server = new ServerSocket(global_port);

      Socket client = server.accept();
      Thread t = new Thread();

      PrintWriter out = new PrintWriter(client.getOutputStream(), true);
      BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

      ip = client.getInetAddress();

      while (br.ready()) {
        username = br.readLine();
        recipient = br.readLine();
      }

      ipMapCheck(username, ip);

      while (true) {

      }
    } catch (IOException e) {
      System.out.println("Connection error.");
      System.exit(0);
    }
  }

  public void ipMapCheck(String username, InetAddress ip){ // this is called on the users name to
    if (!ipMap.containsKey(username)){                     // check they are in the map if not
      ipMap.put(username, ip);                             // put them in
    }
  }

  public int ipMapCheck(String recipient){ // this version is to check the recipient is in the map
    if (ipMap.containsKey(recipient)) {
      return 1;
    } else {
      return 0;
    }
  }

  public int sendMessage(InetAddress ip, String recipient);
}