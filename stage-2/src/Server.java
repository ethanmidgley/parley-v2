import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
  public final int port = 6911;

  HashMap<String, INetAddress> ipMap = new HashMap<>();

  public Server(int port) {
    this.port = port;
  }

  public void running(){
    while (true){

    }

  }

  public void requested(){
    try {
      ServerSocket server = new ServerSocket(server_port);

      Socket client = server.accept();

      while (true){

      }
    } catch (IOException e) {
      System.out.println("Connection error.");
      System.exit(0);
    }
  }

  public void add_username(username){
    if (username not in ipMap){

    }

  }
}