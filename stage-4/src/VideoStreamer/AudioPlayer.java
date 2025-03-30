package VideoStreamer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class AudioPlayer extends Thread {

  private PipedInputStream pipedInputStream;
  private PipedOutputStream pipedOutputStream;
  private final SourceDataLine sourceDataLine;
  private static final int BUFFER_SIZE = 4096;


  public AudioPlayer() throws IOException, LineUnavailableException {

    AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
    this.sourceDataLine = AudioSystem.getSourceDataLine(format);
    sourceDataLine.open(format, BUFFER_SIZE);
    sourceDataLine.start();
    this.pipedOutputStream = new PipedOutputStream();
    this.pipedInputStream = new PipedInputStream();
    pipedInputStream.connect(pipedOutputStream);

  }

  public void write(byte[] audio) throws IOException {
    pipedOutputStream.write(audio);
  }

  public void play() {

    for (;;) {

      try {

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = pipedInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
          sourceDataLine.write(buffer, 0, bytesRead);
        }

      } catch (IOException e) {
        // The shutdown function break out of the play loop
        break;
      }
    }
  }

  public void shutdown() {
    try {
      pipedInputStream.close();
      pipedOutputStream.close();
      sourceDataLine.close();
    } catch (IOException e) {
      System.out.println("AUDIO PLAYER LINE 63 THIS SHOULD NEVER HAPPEN");
      throw new RuntimeException(e);
    }
  }

  public void run() {
    play();
  }
}
