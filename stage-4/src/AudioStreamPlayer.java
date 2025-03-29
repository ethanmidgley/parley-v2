import VideoStreamer.AudioPlayer;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

public class AudioStreamPlayer {

  public static void main(String[] args) throws IOException, LineUnavailableException {

    if (args.length != 1) {
      System.out.println("Usage: java VideoStreamer.AudioPlayer <directory-path>");
      return;
    }

    File directory = new File(args[0]);
    if (!directory.isDirectory()) {
      System.out.println("Invalid directory path.");
      return;
    }

    AudioPlayer p = new AudioPlayer();
    p.start();
    try {

      // Filter for audio files
      File[] audioFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

      if (audioFiles == null || audioFiles.length == 0) {
        System.out.println("No audio files found in the directory.");
        return;
      }

      // Sort files numerically based on filename
      Arrays.sort(audioFiles, Comparator.comparingInt(file -> Integer.parseInt(file.getName().replaceAll("\\D", ""))));

      for (File f: audioFiles) {

        p.write(Files.readAllBytes(f.toPath()));

      }


      System.out.println("Audio player initialized. Write raw audio bytes to play.");
    } catch (Exception e) {
      System.out.println("Error initializing audio stream player.");
      e.printStackTrace();
    }
  }

}
