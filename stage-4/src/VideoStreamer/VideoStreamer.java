package VideoStreamer;

import VideoStreamer.Chunkman.Chunk;
import VideoStreamer.Chunkman.Chunkman;
import VideoStreamer.Chunkman.VideoAudioPair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


//TODO: Restructure into separate components for listening and sending
public class VideoStreamer extends Thread {

  DatagramSocket socket;
  InetAddress peer;
  Chunkman chunkman;
  DataRecievedEvent event;
  int port;


  public VideoStreamer(InetAddress peer, int port, DataRecievedEvent event) throws SocketException {
    this.peer = peer;
    this.port = port;
    this.event = event;
    this.socket = new DatagramSocket(port);
    this.chunkman = new Chunkman();
  }


  // we also need to define a send function
  public void send(byte[] video, byte[] audio) throws IOException {


    Chunk[] chunks = this.chunkman.split(video, audio);
    for (Chunk chunk : chunks) {

//      System.out.println("SENT GROUP :"+ chunk.getChunk_group()+" INDEX: " + chunk.getIndex()+ " FRAME SIZE: " + chunk.getFrame_size());
      byte[] serializedMessage = chunk.toByteArray();

      // create a packet, can only send UDP packets and not text
      DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, peer, port);
      socket.send(packet);

    }


    // we need to compress (optional)

    // chunk and construct each packet

    // send each chunk

  }

  // we also need to define a recieve function
  public void listen() {

    for (;;) {


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
//        System.out.println("RECIEVED GROUP :"+ c.getChunk_group()+" INDEX: " + c.getIndex() + " FRAME SIZE: " + c.getFrame_size());
        VideoAudioPair v = this.chunkman.addChunk(c);
        if (v != null) {
          event.trigger(v);
        }






//        // recieving end
//        ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(recBytes));
//        Message messageClass = (Message) iStream.readObject();
//        iStream.close();


        // add chuck to the chunk map

        // then if complete chunk rebuild and gives back data

        // should probably handle the data in something


      }
      catch(IOException e) {
        System.out.println("ERORR");

      }

    }

  }



  @Override
  public void run () {
    this.listen();
  }

}