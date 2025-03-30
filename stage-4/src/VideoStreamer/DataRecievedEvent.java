package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;

public interface DatareceivedEvent {
  void trigger(VideoAudioPair videoAudioPair);
}
