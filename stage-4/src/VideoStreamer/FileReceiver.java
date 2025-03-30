package VideoStreamer;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import VideoStreamer.Chunkman.VideoAudioPair;

// ill extend thread later
public class FileReceiver {

  private AddressReference peer;
  private final short PORT_NUMBER = 7325;
  private final short TERMINATION_PORT_NUMBER = 4000;
  private VideoStreamer vs;
  private StreamPlayer player;

  private TerminableSocket terminableSocket;
  private TerminationEvent event;
  private AtomicBoolean interrupted;

  public FileReceiver() throws IOException, LineUnavailableException {
    //set peer to null will, be updated during listening
    peer = new AddressReference(null);

    this.player = new StreamPlayer("Receive stream");
    this.terminableSocket = new TerminableSocket(peer, TERMINATION_PORT_NUMBER);
    this.interrupted = new AtomicBoolean(false);

    //construct video streamer and start to listen for incoming webcam video data
    vs = new VideoStreamer(peer,PORT_NUMBER,(VideoAudioPair vap) -> {

      if (!player.playing) {
        player.start();
      }

      player.addFrame(vap);

    });


    this.event = () -> {
      if (!interrupted.get()) {
        interrupted.set(true);
        try {
          this.vs.shutdown();
          this.player.shutdown();
          this.terminableSocket.shutdownPeer();
          this.terminableSocket.shutdown();
          this.peer = null;
        } catch (IOException e) {
          JOptionPane.showMessageDialog(null, "Failed to shutdown some resources, you are good to go the person you were streaming to aren't", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    };


    this.player.bindTerminationEvent(event);
    this.terminableSocket.bindTerminationEvent(event);


  }

  public void start() {
    this.vs.start();
    this.terminableSocket.start();
  }

  public void shutdown() {
    this.event.terminate();
  }

}
