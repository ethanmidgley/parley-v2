package VideoStreamer;
import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_UNCHANGED;



public class StreamPlayer extends Thread {

  private BlockingQueue<VideoAudioPair> images;
  private BlockingQueue<VideoAudioPair> audios;

  private CanvasFrame canvasFrame;
  private final OpenCVFrameConverter.ToMat matConverter;
  private long timestamp;
  private static final int FPS = 24;
  private final AudioPlayer audioPlayer;

  public StreamPlayer(String title) throws LineUnavailableException, IOException {
    this.timestamp = 0;
    this.audioPlayer = new AudioPlayer();
    this.audioPlayer.start();
    this.images = new LinkedBlockingQueue<VideoAudioPair>();
    this.audios = new LinkedBlockingQueue<VideoAudioPair>();
    this.canvasFrame = new CanvasFrame(title);
    this.matConverter = new OpenCVFrameConverter.ToMat();
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

  public void run() {
    Thread audioThread = new Thread(this::playAudio);
    Thread videoThread = new Thread(this::playVideo);

    videoThread.start();
    audioThread.start();

  }

  public void playAudio()  {
    for (;;) {
      try {

        VideoAudioPair videoAudioPair = audios.take();
//        Thread.sleep((videoAudioPair.timestamp - audio_timestamp) / 1000);
//        System.out.println(videoAudioPair.timestamp - timestamp);
//        Thread.sleep(Math.max(0,(videoAudioPair.timestamp - timestamp)));
//
//
//        Thread.sleep((videoAudioPair.timestamp - timestamp) / 1000);
//        System.out.println("audio");
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
  }

  public void playVideo() {
    for (;;) {
      try {
        VideoAudioPair videoAudioPair = images.take();

        Thread.sleep((videoAudioPair.timestamp - timestamp) / 1000);
//        while(videoAudioPair.timestamp > timestamp) {}
        Mat receivedMat = opencv_imgcodecs.imdecode(new Mat(videoAudioPair.video),IMREAD_UNCHANGED);
        canvasFrame.showImage(matConverter.convert(receivedMat));
        this.timestamp = videoAudioPair.timestamp;

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
