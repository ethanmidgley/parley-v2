package VideoStreamer;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import VideoStreamer.Chunkman.VideoAudioPair;

// ill extend thread later
public class FileReceiver{

  private InetAddress peer;
  private final short PORT_NUMBER = 7325;
  private final short TERMINATION_PORT_NUMBER = 4000;
  private VideoStreamer vs;
  private StreamPlayer player;
  private DatagramSocket terminationSocket;
  private AtomicBoolean running;

  public FileReceiver() throws IOException, LineUnavailableException {

    this.running = new AtomicBoolean(true);
    this.player = new StreamPlayer("Receive stream", this.running);
    this.terminationSocket = new DatagramSocket(TERMINATION_PORT_NUMBER);

    //set peer to null will, be updated during listening
    peer = null;
    //construct video streamer and start to listen for incoming webcam video data
    vs = new VideoStreamer(peer,PORT_NUMBER,(VideoAudioPair vap) -> {

      if (!player.playing) {
        player.start();
      }

      player.addFrame(vap);

    }, running);
    vs.start();
  }

//  private void sendTermination() {
//    System.out.println("sending termination to file streamer, file receiver line 45");
//    try {
//      //account for peer being null
//      DatagramSocket dgs = new DatagramSocket(TERMINATION_PORT_NUMBER);
//      DatagramPacket dap = new DatagramPacket(new byte[255], 255,peer,TERMINATION_PORT_NUMBER);
//      for(int i = 0; i < 10; i++) {
//        System.out.println("sending termination packet");
//        dgs.send(dap);
//        Thread.sleep(100);
//      }
//      System.out.println("sent the termination signal, FileReceiver line 52");
//      dgs.close();
//    } catch (SocketException e) {
//      throw new RuntimeException(e);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    } catch (InterruptedException e) {
//      throw new RuntimeException(e);
//    }
//  }

//  private void termination_listener() {
//    //this thread should run for until a small packet is received and this.running will be set to false
//    new Thread(() -> {
//      try {
//        byte[] buffer = new byte[255];
//        DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
//        this.terminationSocket.receive(datagramPacket);
//        if(datagramPacket.getAddress().equals(peer)) {
//          System.out.println("received termination from file streamer: file receiver line 59");
//          this.running.set(false);
//          this.terminationSocket.close();
//        }
//        else {
//          System.out.println("someone outside is trying to kill connection");
//        }
//      } catch (SocketException e) {
//        e.printStackTrace();
//      } catch (IOException e) {
//        throw new RuntimeException(e);
//      }
//    }).start();
//  }

  //FIXME: before even running this could terminate the next stream being received due to synchronisation
  public void shutdown() throws InterruptedException {
    //TODO: account for packet loss tomorrow and reduce number of shutdown calls
    System.out.println("shutting down file receiver line 86");
    player.shutdown();
    this.vs.shutdownStreamer();
    this.terminationSocket.close();
    this.running.set(false);
  }
}
