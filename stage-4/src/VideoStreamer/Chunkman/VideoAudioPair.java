package VideoStreamer.Chunkman;

public class VideoAudioPair {
  public byte[] video;
  public byte[] audio;
  public long timestamp;

  public VideoAudioPair(long timestamp, byte[] video, byte[] audio) {
    this.timestamp = timestamp;
    this.video = video;
    this.audio = audio;
  }
}
