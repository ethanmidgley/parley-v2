package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_UNCHANGED;

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
    player.start();
    vs = new VideoStreamer(peer,PORT_NUMBER,player::addFrame,running);

//    this.termination_listener();
    vs.start();
  }

  private void sendTermination() {
    System.out.println("sending termination to file streamer, file receiver line 45");
    try {
      //account for peer being null
      DatagramSocket dgs = new DatagramSocket(11000);
      DatagramPacket dap = new DatagramPacket(new byte[255], 255,peer,TERMINATION_PORT_NUMBER);
      dgs.send(dap);
      dgs.close();
    } catch (SocketException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

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
//    this.sendTermination();
    this.terminationSocket.close();
    this.running.set(false);
  }
}
