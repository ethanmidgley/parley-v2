package VideoStreamer;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Speaker {

  private static final int BUFFER_SIZE = 4096;
 public static void out(byte[] outstream) {

    try {

      ByteArrayInputStream bin = new ByteArrayInputStream(outstream);
      AudioInputStream audioStream = AudioSystem.getAudioInputStream(bin);

      AudioFormat format = audioStream.getFormat();

//      AudioFormat format = new AudioFormat(44100, 16, 1, true, false);

      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

      SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);

      audioLine.open(format);

      audioLine.start();

      byte[] bytesBuffer = new byte[BUFFER_SIZE];
      int bytesRead = -1;

      while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
        audioLine.write(bytesBuffer, 0, bytesRead);
      }

//      try {
//        Thread.sleep(1000 / 15);
//      }
//      catch (InterruptedException e) {}

      audioLine.drain();
      audioLine.close();
      audioStream.close();


    } catch (UnsupportedAudioFileException ex) {
      System.out.println("The specified audio file is not supported.");
      ex.printStackTrace();
    } catch (LineUnavailableException ex) {
      System.out.println("Audio line for playing back is unavailable.");
      ex.printStackTrace();
    } catch (IOException ex) {
      System.out.println("Error playing the audio file.");
      ex.printStackTrace();
    }

  }

}
