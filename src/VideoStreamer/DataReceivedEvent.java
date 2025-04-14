package VideoStreamer;

import VideoStreamer.Chunkman.VideoAudioPair;

public interface DataReceivedEvent {
  void trigger(VideoAudioPair videoAudioPair);
}
