package VideoStreamer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

import javax.sound.sampled.*;


public class localAudioPlayTest {
  public static void main(String[] args) {
    BlockingQueue<Frame> audioBuffer = new LinkedBlockingQueue<>(); // Adjust size as needed
    final int BUFFER_SIZE = 4096;
    AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, false);

    FFmpegFrameGrabber fGrabber = new FFmpegFrameGrabber("./cico.mp4");

    Thread adt = new Thread(() -> {
      try {
        fGrabber.start();

        Frame frame;
        while((frame = fGrabber.grabFrame()) != null) {
          if (frame.samples != null) {
            audioBuffer.put(frame);
          }
        }

        fGrabber.stop();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    adt.start();

    try {
      // Open TargetDataLine (input from microphone)
      DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
      TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
      targetLine.open(format);
      targetLine.start();

      // Open SourceDataLine (output to speakers)
      DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
      SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
      sourceLine.open(format);
      sourceLine.start();

      // Buffer for transferring audio
      byte[] buffer = new byte[BUFFER_SIZE];

      System.out.println("Audio loopback started. Speak into the microphone...");

      // Read from microphone and write to speakers
      while (true) {
        int bytesRead = targetLine.read(buffer, 0, buffer.length);
        if (bytesRead > 0) {
          sourceLine.write(buffer, 0, bytesRead);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
