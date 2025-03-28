package VideoStreamer.Chunkman;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Chunk {

  private final int chunk_group;
  private final int index;
  private final int total_group_chunks;
  private final long timestamp;
  private final byte[] frame;
  private final int frame_size;
  private final byte[] audio;
  private final int audio_size;

  private final static int HEADER_SIZE = 28;


  public Chunk(int chunk_group, int index, int total_group_chunks, long timestamp, byte[] frame, byte[] audio) {
    this.frame = frame;
    this.audio = audio;
    this.chunk_group = chunk_group;
    this.index = index;
    this.total_group_chunks = total_group_chunks;
    this.timestamp = timestamp;
    this.frame_size = this.frame.length;
    this.audio_size = this.audio.length;
  }



  // MAX SIZE IS 65507 bytes
  public Chunk(byte[] byteArray) {
    if (byteArray.length > 65507) {
      throw new IllegalArgumentException("Chunk length is too large");
    }

    this.chunk_group = ByteBuffer.wrap(byteArray,0, 4).getInt();
    this.index = ByteBuffer.wrap(byteArray,4, 4).getInt();
    this.total_group_chunks = ByteBuffer.wrap(byteArray,8, 4).getInt();
    this.timestamp = ByteBuffer.wrap(byteArray,12, 8).getLong();
    this.frame_size = ByteBuffer.wrap(byteArray,20, 4).getInt();
    this.audio_size = ByteBuffer.wrap(byteArray,24, 4).getInt();

    // frame size + 1
    int offset = HEADER_SIZE;
    this.frame = Arrays.copyOfRange(byteArray, offset, offset + this.frame_size);

    offset += this.frame_size;

    this.audio = Arrays.copyOfRange(byteArray, offset, offset + this.audio_size);

  }


  public byte[] toByteArray() {
    byte[] a = new byte[65507];

    ByteBuffer bb = ByteBuffer.wrap(a);
    bb.putInt(0, this.chunk_group);
    bb.putInt(4, this.index);
    bb.putInt(8, this.total_group_chunks);
    bb.putLong(12, this.timestamp);
    bb.putInt(20, this.frame_size);
    bb.putInt(24, this.audio_size);

    int offset = HEADER_SIZE;
    bb.put(offset,this.frame, 0, this.frame_size);
    offset += this.frame_size;
    bb.put(offset, this.audio, 0, this.audio_size);

    return a;

  }

  public long getTimestamp() {
    return timestamp;
  }

  public byte[] getFrame() {
    return frame;
  }

  public byte[] getAudio() {
    return audio;
  }

  public int getChunkGroup() {
    return chunk_group;
  }

  public int getIndex() {
    return index;
  }

  public int getTotalGroupChunks() {
    return total_group_chunks;
  }

  public boolean isAudio () {
    return audio_size > 0;
  }

  public boolean isFrame() {
    return frame_size > 0;
  }

  public int getFrameSize() {
    return frame_size;
  }

  public int getAudioSize() {
    return audio_size;
  }
  public String toString() {return (chunk_group +" "+index+" "+this.total_group_chunks+" "+frame_size+" "+audio_size);}
}
