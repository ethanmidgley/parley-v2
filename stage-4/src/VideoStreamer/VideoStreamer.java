package VideoStreamer;

import VideoStreamer.Chunkman.Chunk;
import VideoStreamer.Chunkman.Chunkman;
import VideoStreamer.Chunkman.VideoAudioPair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;


//TODO: Restructure into separate components for listening and sending
public class VideoStreamer extends Thread {

  DatagramSocket socket;
  InetAddress peer;
  Chunkman chunkman;
  DataRecievedEvent event;
  int port;
  int send_port;

  private AtomicBoolean running;


  public VideoStreamer(InetAddress peer, int port, DataRecievedEvent event, AtomicBoolean running) throws SocketException {
    this.peer = peer;
    this.port = port;
    this.send_port = port;
    this.event = event;
    this.socket = new DatagramSocket(port);
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

  // we also need to define a recieve function
  public void listen() {

    while(running.get()) {


      byte[] buffer = new byte[65507];


      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      try {
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
      catch(IOException e) {
        System.out.println("ERORR");

      }

    }
    System.out.println("running: " + running);

  }



  @Override
  public void run () {
    this.listen();
  }

}