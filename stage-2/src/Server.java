import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server{
  private int READING_PORT = 8085;
  private int WRITING_PORT = 8008;
  private String username;
  private InetAddress ip;
  HashMap<String, InetAddress> ipMap = new HashMap<>();

  public Server(int READING_PORT, int WRITING_PORT) {
    this.READING_PORT = READING_PORT;
    this.WRITING_PORT = WRITING_PORT;
  }
}