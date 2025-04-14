package ConnectionListener;

import ClientDirectory.ClientDirectory;
import ConnectedClient.*;
import MessageQueue.MessageQueue;
import OnlineCount.OnlineCount;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPConnectionListener extends ConnectionListener {

  private final ServerSocket server;
  private final ClientDirectory directory;
  private OnlineCount onlineCount;

  private final MessageQueue mq;

  public TCPConnectionListener(ClientDirectory directory, MessageQueue mq, int port, OnlineCount onlineCount) throws IOException {
    this.mq = mq;
    this.server = new ServerSocket(port);
    this.directory = directory;
    this.onlineCount = onlineCount;
  }

  public void listen()  {
    while (true) {

      Socket client = null;

      try {
        // TODO: if .accept throws exception then the listening socket is no longer open
        client = server.accept();
      } catch(IOException e) {
        System.out.println("Server listening socket closed...");;
      }

      if(client != null) {
        try {
          ConnectedClient c = new TCPConnectedClient(client, directory,mq,onlineCount);
          c.start();
        } catch (IOException e) {
          // TODO: if new TCPConnectectClient throws exception then we failed to establish two way communication with client
          System.out.println("failed to establish client connection to the server...");
        }
      }
    }
  }
}
