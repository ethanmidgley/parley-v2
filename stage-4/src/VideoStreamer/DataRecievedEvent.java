package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;

public interface DataRecievedEvent {
  void trigger(VideoAudioPair videoAudioPair);
}
