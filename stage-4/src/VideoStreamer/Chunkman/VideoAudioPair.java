package VideoStreamer.Chunkman;

public class VideoAudioPair {
  public byte[] video;
  public byte[] audio;

  public VideoAudioPair(byte[] video, byte[] audio) {
    this.video = video;
    this.audio = audio;
  }
}
