package VideoStreamer;

import VideoStreamer.Chunkman.Chunk;
import VideoStreamer.Chunkman.Chunkman;
import VideoStreamer.Chunkman.VideoAudioPair;

import java.io.IOException;
import java.net.*;


//TODO: Restructure into separate components for listening and sending
public class VideoStreamer extends Thread {

  DatagramSocket socket;
  DatagramSocket send_socket;
  AddressReference peer;
  Chunkman chunkman;
  DataReceivedEvent event;
  int port;
  int send_port;

  public VideoStreamer(AddressReference peer, int port) throws SocketException {
    this.peer = peer;
    this.port = port;
    this.send_port = port;
    this.event = (DataReceivedEvent) event;
    this.socket = new DatagramSocket(port);
    this.send_socket = new DatagramSocket(9999);
    this.chunkman = new Chunkman();
  }


  public VideoStreamer(AddressReference peer, int port, DataReceivedEvent event) throws SocketException {
    this.peer = peer;
    this.port = port;
    this.send_port = port;
    this.event = event;
    this.socket = new DatagramSocket(port);
    this.send_socket = new DatagramSocket(9999);
    this.chunkman = new Chunkman();
  }

  public VideoStreamer(AddressReference peer, int listen_port, int send_port, DataReceivedEvent event) throws SocketException {
    this.peer = peer;
    this.port = listen_port;
    this.send_port = send_port;
    this.event = event;
    this.socket = new DatagramSocket(port);
    this.send_socket = new DatagramSocket(9999);
    this.chunkman = new Chunkman();
  }


  // we also need to define a send function
  public void send(byte[] video, byte[] audio, long timestamp) throws IOException {


    Chunk[] chunks = this.chunkman.split(video, audio, timestamp);
    for (Chunk chunk : chunks) {

//      System.out.println("SENT GROUP :"+ chunk.getChunk_group()+" INDEX: " + chunk.getIndex()+ " FRAME SIZE: " + chunk.getFrame_size());
      byte[] serializedMessage = chunk.toByteArray();

      // create a packet, can only send UDP packets and not text
      DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, peer.getAddress(), send_port);
      send_socket.send(packet);

    }

  }

  // we also need to define a receive function
  public void listen() {

    for (;;) {


      byte[] buffer = new byte[65507];


      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      try {
        //assuming timeout is from a close
        socket.receive(packet);
        peer.setAddress(packet.getAddress());
//        if(peer.getAddress() == null) {
//          peer.setAddress(packet.getAddress());
//        }
//        if (!packet.getAddress().equals(peer.getAddress())) {
//          System.out.println(packet.getAddress());
//          System.out.println(peer.getAddress());
////          System.out.println("Packet interference caught, someone sneaky is lurking");
//          continue;
//        }

        Chunk c = new Chunk(packet.getData());
        VideoAudioPair v = this.chunkman.addChunk(c);
        if (v != null) {
          event.trigger(v);
        }

      }
      catch(IOException e) {
        System.out.println("we got to the io exception: line 104 video streamer");;
        break;
      }
    }
  }


  //close the socket for sending
  public void shutdown() {
   socket.close();
   send_socket.close();
  }

  @Override
  public void run () {
    this.listen();
  }

}