package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.ShortBuffer;

public class FileStreamer extends Thread{
  private final FFmpegFrameGrabber videoGrabber;
  private final File video;

  private final OpenCVFrameConverter.ToMat matConverter;
  private final short FRAME_RATE = 60;
  private final short RECIPIENT_PORT_NUMBER = 7325;
  private final short PORT_NUMBER = 7326;
  private final VideoStreamer vs;
  private final StreamPlayer p;

  private boolean[] running;


  public FileStreamer(InetAddress peer,File f) throws IOException, LineUnavailableException {
    //webcam variables
    this.video = f;
    this.matConverter = new OpenCVFrameConverter.ToMat();
    this.videoGrabber = new FFmpegFrameGrabber(video);
    this.videoGrabber.setFrameRate(10);
    this.videoGrabber.setAudioChannels(1); //mono
    this.videoGrabber.start();
    this.running = new boolean[]{true};


    this.p = new StreamPlayer("Video Stream", running);
    p.start();

    vs = new VideoStreamer(peer, PORT_NUMBER, RECIPIENT_PORT_NUMBER, p::addFrame, this.running);
    vs.start();

  }

  public void shutdown() {
    this.running[0] = false;
    System.out.println("thread stopped running");
  }

  @Override
  public void run() {

    while(running[0]) {
      try {

        Frame frame = videoGrabber.grabFrame();

        if (frame.samples != null) {


          ShortBuffer shortBuffer = (ShortBuffer) frame.samples[0];
          byte[] audioBytes = new byte[shortBuffer.remaining() * 2]; // 2 bytes per short
          for (int i = 0; i < shortBuffer.remaining(); i++) {
            short sample = shortBuffer.get(i);
            audioBytes[i * 2] = (byte) (sample & 0xFF); // Lower byte
            audioBytes[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF); // Higher byte
          }


//          AudioFormat format = new AudioFormat(videoGrabber.getSampleRate(), 16, videoGrabber.getAudioChannels(), true, false);

//          byte[] data = AudioEncoder.encode(audioBytes, format);



          vs.send(new byte[0], audioBytes, frame.timestamp);
          p.addFrame(new VideoAudioPair(frame.timestamp, new byte[0], audioBytes));


        }

        if (frame.image != null) {


          Mat m = matConverter.convertToMat(frame);

          BytePointer bp = new BytePointer();
          boolean success = opencv_imgcodecs.imencode(".jpg",m,bp);


          if(success) {

            byte[] compressedData = new byte[(int) bp.limit()];
            bp.get(compressedData);

            vs.send(compressedData,new byte[0], frame.timestamp);
            p.addFrame(new VideoAudioPair(frame.timestamp, compressedData, new byte[0]));
          }
          bp.deallocate();

        }

//          try {
//            Thread.sleep(1000/FRAME_RATE);
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }

        //TODO:update this shit
      } catch (FrameGrabber.Exception e) {
        throw new RuntimeException(e);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    System.out.println("running: " + running[0]);
  }
}
