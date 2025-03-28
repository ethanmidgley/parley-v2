package VideoStreamer;

import VideoStreamer.Chunkman.Chunk;
import VideoStreamer.Chunkman.Chunkman;
import VideoStreamer.Chunkman.VideoAudioPair;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;


//TODO: Restructure into separate components for listening and sending
public class VideoStreamer extends Thread {

  DatagramSocket socket;
  InetAddress peer;
  Chunkman chunkman;
  DataRecievedEvent event;
  int port;
  int send_port;
  private final short TERMINATION_PORT_NUMBER = 4000;
  private final short NUM_SECONDS = 5;

  private AtomicBoolean running;


  public VideoStreamer(InetAddress peer, int port, DataRecievedEvent event, AtomicBoolean running) throws SocketException {
    this.peer = peer;
    this.port = port;
    this.send_port = port;
    this.event = event;
    this.socket = new DatagramSocket(port);
    this.socket.setSoTimeout(NUM_SECONDS * 1000);
    this.chunkman = new Chunkman();
    this.running = running;
  }


  public VideoStreamer(InetAddress peer, int listen_port, int send_port, DataRecievedEvent event, AtomicBoolean running) throws SocketException {
    this.peer = peer;
    this.port = listen_port;
    this.send_port = send_port;
    this.event = event;
    this.socket = new DatagramSocket(port);
    this.chunkman = new Chunkman();
    this.running = running;
  }


  // we also need to define a send function
  public void send(byte[] video, byte[] audio, long timestamp) throws IOException {


    Chunk[] chunks = this.chunkman.split(video, audio, timestamp);
    for (Chunk chunk : chunks) {

//      System.out.println("SENT GROUP :"+ chunk.getChunk_group()+" INDEX: " + chunk.getIndex()+ " FRAME SIZE: " + chunk.getFrame_size());
      byte[] serializedMessage = chunk.toByteArray();

      // create a packet, can only send UDP packets and not text
      DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, peer, send_port);
      socket.send(packet);

    }

  }

  // we also need to define a receive function
  public void listen() {

    while(running.get()) {


      byte[] buffer = new byte[65507];


      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      try {
        //assuming timeout is from a close
        socket.receive(packet);
        if(peer == null) {
          peer = packet.getAddress();
        }
        if (!packet.getAddress().equals(peer)) {
          System.out.println("Packet interference caught, someone sneaky is lurking");
          continue;
        }

        Chunk c = new Chunk(packet.getData());
        VideoAudioPair v = this.chunkman.addChunk(c);
        if (v != null) {
          event.trigger(v);
        }

      }
      catch(SocketTimeoutException e) {
        System.out.println("socket timed out");
        break;
      }
      catch(SocketException e) {
        System.out.println("socket closed by running flag line 101");
      }
      catch(IOException e) {
        System.out.println("we got to the io exception: line 104 video streamer");;
        break;
      }
      System.out.println("video streamer running " + this.running.get());
    }
    System.out.println("receiver end killed by flag running: " + this.running.get() + ", video streamer line 113");
    while(true) {
      try {
        this.shutdown();
        break;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }


  //close the socket for sending
  public void shutdown() throws InterruptedException {
   socket.close();
   System.out.println("sending termination to file streamer, file receiver line 45");
   //FIXME? this shit might fail if running is set to false form somewhere else
   this.running.set(false);
   try {
     //account for peer being null
     DatagramSocket dgs = new DatagramSocket(TERMINATION_PORT_NUMBER);
     DatagramPacket dap = new DatagramPacket(new byte[255], 255,peer,TERMINATION_PORT_NUMBER);
     for(int i = 0; i < 10; i++) {
       dgs.send(dap);
     }
     dgs.close();
   } catch (SocketException e) {
     throw new RuntimeException(e);
   } catch (IOException e) {
     throw new RuntimeException(e);
   }
  }

  @Override
  public void run () {
    this.listen();
  }

}