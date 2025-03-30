package Tests;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class WavTest {


  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java VideoStreamer.AudioPlayer <directory-path>");
      return;
    }

    File directory = new File(args[0]);
    if (!directory.isDirectory()) {
      System.out.println("Invalid directory path.");
      return;
    }

    // Filter for audio files
    File[] audioFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

    if (audioFiles == null || audioFiles.length == 0) {
      System.out.println("No audio files found in the directory.");
      return;
    }

    // Sort files numerically based on filename
    Arrays.sort(audioFiles, Comparator.comparingInt(file -> Integer.parseInt(file.getName().replaceAll("\\D", ""))));

    try (SourceDataLine line = AudioSystem.getSourceDataLine(AudioSystem.getAudioInputStream(audioFiles[0]).getFormat())) {
      line.open();
      line.start();

      for (File file : audioFiles) {
        System.out.println("Playing: " + file.getName());
        playAudio(file, line);
      }

      line.drain();
      line.close();
    } catch (Exception e) {
      System.out.println("Error initializing audio playback.");
      e.printStackTrace();
    }
  }

  public static void playAudio(File audioFile, SourceDataLine line) {
    try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile)) {
      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = audioStream.read(buffer, 0, buffer.length)) != -1) {
        line.write(buffer, 0, bytesRead);
//        line.flush();
      }
    } catch (Exception e) {
      System.out.println("Error playing file: " + audioFile.getName());
      e.printStackTrace();
    }
  }

}
