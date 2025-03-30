package VideoStreamer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class TerminableSocket extends Thread {

  private DatagramSocket socket;
  private AddressReference peer;
  private TerminationEvent event;
  private int port;
  private boolean receivedTerminate;


  public TerminableSocket(AddressReference peer, int port) throws SocketException {
    this.peer = peer;
    this.port = port;
    this.event = () -> {};
    this.socket = new DatagramSocket(port);
    this.receivedTerminate = false;
  }


  public void bindTerminationEvent(TerminationEvent event) {
    this.event = event;
  }

  public void run() {

    for (;;) {

      byte[] buffer = new byte[255];
      DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
      try {
        this.socket.receive(datagramPacket);

        if(datagramPacket.getAddress().equals(peer.getAddress())) {
          System.out.println("received termination instruction from receiver: file streamer line 86");

          // received a termination packet so call terminate
          receivedTerminate = true;
          this.event.terminate();

          // I want to break free - Freddie Mercury
          break;
        }
        else {
          System.out.println("fuck you saids the fuck you guy");
        }


      } catch (IOException e) {

        // We get some error so lets stop all streaming
        this.event.terminate();
        break;

      }
    }
  }

  public void shutdownPeer() throws IOException {

    if (!receivedTerminate) {
      //account for peer being null
      DatagramSocket dgs = new DatagramSocket();
      DatagramPacket dap = new DatagramPacket(new byte[255], 255,peer.getAddress(),port);
      dgs.send(dap);
      dgs.close();
    }

  }

  public void shutdown() {
    this.socket.close();
  }

}
