package VideoStreamer;
import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import javax.sound.sampled.LineUnavailableException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_UNCHANGED;



public class StreamPlayer extends Thread {

  private BlockingQueue<VideoAudioPair> images;
  private BlockingQueue<VideoAudioPair> audios;

  private CanvasFrame canvasFrame;
  private final OpenCVFrameConverter.ToMat matConverter;
  private final AudioPlayer audioPlayer;
  private AtomicBoolean running;

  private long timestamp;
  private final boolean sync;
  public boolean playing;

  public StreamPlayer(String title, AtomicBoolean running) throws LineUnavailableException, IOException {
    this.timestamp = 0;
    this.audioPlayer = new AudioPlayer(running);
    this.audioPlayer.start();
    this.images = new LinkedBlockingQueue<VideoAudioPair>();
    this.audios = new LinkedBlockingQueue<VideoAudioPair>();
    this.running = new AtomicBoolean(true);
    this.canvasFrame = new CanvasFrame(title);
    this.matConverter = new OpenCVFrameConverter.ToMat();
    this.sync = true;
    this.playing = false;
    this.running = running;

    this.canvasFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
//        running[0] = false;
        StreamPlayer.this.shutdown();
      }
    });
  }

  public StreamPlayer(String title, AtomicBoolean running, boolean sync) throws LineUnavailableException, IOException {
    this.timestamp = 0;
    this.audioPlayer = new AudioPlayer(running);
    this.audioPlayer.start();
    this.images = new LinkedBlockingQueue<VideoAudioPair>();
    this.audios = new LinkedBlockingQueue<VideoAudioPair>();
    this.canvasFrame = new CanvasFrame(title);
    this.matConverter = new OpenCVFrameConverter.ToMat();
    this.sync = sync;
    this.playing = false;
    this.running = running;
  }



  public void addFrame(VideoAudioPair videoAudioPair) {
    if (videoAudioPair.video.length > 0 && videoAudioPair.audio.length > 0) {
      System.err.println("We send audio and videos together fix this condition");
      System.exit(0);
    }

    if (videoAudioPair.video.length > 0) {
      // video frame
      images.add(videoAudioPair);
    }

    if (videoAudioPair.audio.length > 0) {
      audios.add(videoAudioPair);
    }

  }

  public void shutdown() {
    System.out.println("shutting down streamplayer: line 71");
    this.running.set(false);
    this.canvasFrame.dispose();
  }

  public void run() {

    this.playing = true;

    Thread audioThread = new Thread(this::playAudio);
    audioThread.start();

    if (sync) {

      Thread timerThread = new Thread(this::timer);
      timerThread.start();

      Thread videoThread = new Thread(this::playVideoSync);
      videoThread.start();

    } else {
      Thread videoThread = new Thread(this::playVideoNoSync);
      videoThread.start();
    }

  }

  public void timer() {
    // we can update the timestamp every 50ms or so it should be fine maybe
    long current_time = System.currentTimeMillis();
    while (this.running.get()) {

      try {

        Thread.sleep(50);

        long now = System.currentTimeMillis();
        this.timestamp += (now - current_time);
        current_time = now;

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

    }


  }

  public void playAudio()  {
    while(this.running.get()) {
      try {

        VideoAudioPair videoAudioPair = audios.take();

        try {
          this.audioPlayer.write(videoAudioPair.audio);
        } catch (IOException e) {
          // TODO: THIS SHOULD BE HANDLED BETTER
          throw new RuntimeException(e);
        }

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    System.out.println("shutting audio player, stream player line 108");
    audioPlayer.shutdown();
  }

  public void playVideoNoSync() {
    while(this.running.get()) {
      try {
        VideoAudioPair videoAudioPair = images.take();
        Mat receivedMat = opencv_imgcodecs.imdecode(new Mat(videoAudioPair.video), IMREAD_UNCHANGED);
        Frame frame = matConverter.convert(receivedMat);

        canvasFrame.showImage(frame);

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }


  public void playVideoSync() {
    while (this.running.get()) {
      try {
        VideoAudioPair videoAudioPair = images.take();
        Mat receivedMat = opencv_imgcodecs.imdecode(new Mat(videoAudioPair.video),IMREAD_UNCHANGED);
        Frame frame = matConverter.convert(receivedMat);

        // NOT BAD
//        long time_til_frame = (videoAudioPair.timestamp - timestamp) / 1000;
//        Thread.sleep(Math.max(time_til_frame - 10, 0));


//        Not terrible but certainly could be better scared for testing on other machines
//        long time_til_frame = (videoAudioPair.timestamp - timestamp) / 1000;
//        Thread.sleep(Math.max((int) (time_til_frame * 0.74), 0));

        while (videoAudioPair.timestamp > (timestamp * 1000)) {
          Thread.sleep(20);
//          System.out.println(videoAudioPair.timestamp);
        }



        canvasFrame.showImage(frame);
//        this.timestamp = videoAudioPair.timestamp;

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    System.out.println("shutting down the video player, stream player line 127");
  }
}
