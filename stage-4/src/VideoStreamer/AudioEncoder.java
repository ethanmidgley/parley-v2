package VideoStreamer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class AudioEncoder {

  public static byte[] encode(byte[] data, AudioFormat format) {


    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      AudioInputStream ais = new AudioInputStream(bais, format, data.length);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      AudioSystem.write(ais, AudioFileFormat.Type.WAVE, baos);
      return baos.toByteArray();
    } catch (Exception e) {
    }

    return null;

  }


  public static byte[] encode(ByteBuffer byteBuffer, AudioFormat format) {

    byte[] data = new byte[byteBuffer.remaining()];
    byteBuffer.get(data);

    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      AudioInputStream ais = new AudioInputStream(bais, format, data.length);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      AudioSystem.write(ais, AudioFileFormat.Type.WAVE, baos);
      return baos.toByteArray();
    } catch (Exception e) {
    }

    return null;

  }

}
