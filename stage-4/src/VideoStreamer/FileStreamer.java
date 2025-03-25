package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_UNCHANGED;

public class FileStreamer extends Thread{
  private final FFmpegFrameGrabber videoGrabber;
  private final CanvasFrame canvasFrame;
  private final File video;

  private final OpenCVFrameConverter.ToMat matConverter;
  private final short FRAME_RATE = 60;
  private final short RECIPIENT_PORT_NUMBER = 7326;
  private VideoStreamer vs;


  public FileStreamer(InetAddress peer,File f) throws SocketException, FrameGrabber.Exception {
    //webcam variables
    this.video = f;
    this.videoGrabber = new FFmpegFrameGrabber(video);
    this.videoGrabber.setFrameRate(10);
    this.videoGrabber.setAudioChannels(1); //mono
    this.videoGrabber.start();
    this.canvasFrame = new CanvasFrame("webcam");
    matConverter = new OpenCVFrameConverter.ToMat();

    StreamPlayer p = new StreamPlayer("please");
    p.start();
    //construct video streamer and start to listen for incoming webcam video data

    vs = new VideoStreamer(peer,RECIPIENT_PORT_NUMBER,(VideoAudioPair vap) -> {
      p.addFrame(vap);

//      if (vap.audio.length > 0) {
//        System.out.println("HELLLOADFAFADLKFJ:");
//        Speaker.out(vap.audio);
//      }

      return;
//      Mat receivedMat = opencv_imgcodecs.imdecode(new Mat(vap.video),IMREAD_UNCHANGED);
//      canvasFrame.showImage(matConverter.convert(receivedMat));
    });
    vs.start();
  }

  @Override
  public void run() {
    long startTime = System.nanoTime();
    long videoStartTime = videoGrabber.getTimestamp();
    int j = 0;

    for(;;) {
      try {

        Frame frame = videoGrabber.grabFrame();

        if (frame.samples != null) {

          Buffer a = frame.samples[0];

//          ShortBuffer shortBuffer = (ShortBuffer)  a;
//          ByteBuffer byteBuffer = ByteBuffer.allocate(shortBuffer.capacity() * 2);
//          byteBuffer.asShortBuffer().put(shortBuffer);

          ShortBuffer shortBuffer = (ShortBuffer) frame.samples[0];
          byte[] audioBytes = new byte[shortBuffer.remaining() * 2]; // 2 bytes per short
          for (int i = 0; i < shortBuffer.remaining(); i++) {
            short sample = shortBuffer.get(i);
            audioBytes[i * 2] = (byte) (sample & 0xFF); // Lower byte
            audioBytes[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF); // Higher byte
          }


          AudioFormat format = new AudioFormat(videoGrabber.getSampleRate(), 16, videoGrabber.getAudioChannels(), true, false);

          byte[] data = AudioEncoder.encode(audioBytes, format);

//
//          FileOutputStream fos = new FileOutputStream("./debug/"+ (j++) + ".wav");
//          fos.write(data);


          vs.send(new byte[0], data, frame.timestamp);

        }

        if (frame.image != null) {


          Mat m = matConverter.convertToMat(frame);
          canvasFrame.showImage(frame);

          BytePointer bp = new BytePointer();
          boolean success = opencv_imgcodecs.imencode(".jpg",m,bp);


          if(success) {

            byte[] compressedData = new byte[(int) bp.limit()];
            bp.get(compressedData);

            vs.send(compressedData,new byte[0], frame.timestamp);
          }
          bp.deallocate();

        }

//        long currentVideoTime = videoGrabber.getTimestamp() - videoStartTime;
//        long currentTime = (System.nanoTime() - startTime) / 1000; // Convert to microseconds
//
//        long delay = currentVideoTime - currentTime;
//        if (delay > 0) {

//          try {
////          Thread.sleep(delay / 1000, (int) (delay % 1000) * 1000);
//            Thread.sleep(delay / 1000);
//
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }
//        }

          try {
            Thread.sleep(1000/FRAME_RATE);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

        //TODO:update this shit
      } catch (FrameGrabber.Exception e) {
        throw new RuntimeException(e);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
