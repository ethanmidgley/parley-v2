package VideoStreamer.Chunkman;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Chunkman {

  private int next_split_id;
  private Map<Integer, List<Chunk>> chunks;
  private int last_rebuild = -1;

  private static int MAX_CHUNK_SIZE = 65507;
  private static int HEADING_SIZE = 28;
  private static int MAX_DATA_SIZE = MAX_CHUNK_SIZE - HEADING_SIZE;

  //write chunk and unchunk methods

  public Chunkman() {
    this.next_split_id = 0;
    this.chunks = new ConcurrentHashMap<>();
  }


  public VideoAudioPair rebuildFromChunk(Chunk chunk) {
    return new VideoAudioPair(chunk.getTimestamp(),chunk.getFrame(), chunk.getAudio());
  }

  public VideoAudioPair rebuildFromChunks(List<Chunk> chunks) {

      chunks.sort(Comparator.comparing(Chunk::getIndex));

      int video_length = 0;
      int audio_length = 0;
      for (Chunk chunk : chunks) {
        video_length += chunk.getFrameSize();
        audio_length += chunk.getAudioSize();
      }

      byte[] video_data = new byte[video_length];
      byte[] audio_data = new byte[audio_length];

      int video_offset = 0;
      int audio_offset = 0;
      for (Chunk chunk : chunks) {

        byte[] frame = chunk.getFrame();
        byte[] audio = chunk.getAudio();

        for (int i = 0; i < frame.length; i++) {
          video_data[i + video_offset] = frame[i];
        }
        video_offset += frame.length;

        for (int i = 0; i < audio.length; i++) {
          audio_data[i + audio_offset] = audio[i];
        }
        audio_offset += audio.length;

      }

      return new VideoAudioPair(chunks.get(0).getTimestamp(),video_data, audio_data);
  }

  public void invalidateChunks(int comparator) {

    Set<Integer> keys = chunks.keySet();
    keys.stream().filter(k -> k <= comparator).forEach(k -> {chunks.remove(k);});

  }


  public VideoAudioPair addChunk(Chunk chunk) {

    // Don't bother saving a chunk if we have already built chunks from after
    if (chunk.getChunkGroup() < last_rebuild) {
      return null;
    }

    if (chunk.getTotalGroupChunks() == 1) {
      // Chunk contains a complete dataset
      // we wanna invalidate as well here
      if (chunk.isFrame()) {
        last_rebuild = chunk.getChunkGroup();
        invalidateChunks(chunk.getChunkGroup());
      }
//      invalidateChunks(last_rebuild);
      return rebuildFromChunk(chunk);
    }

    List<Chunk> group = chunks.get(chunk.getChunkGroup());

    if (group == null) {
      group = new ArrayList<>();
      chunks.put(chunk.getChunkGroup(), group);
    }

    group.add(chunk);

    if (group.size() == chunk.getTotalGroupChunks()) {
      // we have got a complete group
      // Invalidate old chunks


      if (chunk.isFrame()) {
        last_rebuild = chunk.getChunkGroup();
        invalidateChunks(chunk.getChunkGroup());
      }

      return rebuildFromChunks(group);
    }

    // ehh we just saved it i guess
    return null;

  }


  public Chunk[] split(byte[] video, byte[] audio, long timestamp) {
    // 65507

    int total_size = video.length + audio.length;
    int total_chunks = Math.ceilDiv(total_size, MAX_DATA_SIZE);

    int video_offset = 0;
    int audio_offset = 0;

    Chunk[] chunks = new Chunk[total_chunks];

    for (int i = 0; i < total_chunks; i++) {

      byte[] frame_data = new byte[0];
      byte[] audio_data = new byte[0];
      // first n bytes
      if (video_offset < video.length) {
        // 65487
        frame_data = Arrays.copyOfRange(video, video_offset, Math.min(video_offset + MAX_DATA_SIZE, video.length));
        video_offset += frame_data.length;

      }

      int remaining_space = MAX_DATA_SIZE - frame_data.length;
      if (remaining_space > 0) {
        if (audio_offset < audio.length) {
          // we need to take as much possible
          // 65487
          audio_data = Arrays.copyOfRange(audio, audio_offset, Math.min(remaining_space, audio.length));
          audio_offset += audio_data.length;
        }
      }

      chunks[i] = new Chunk(next_split_id, i, total_chunks, timestamp, frame_data, audio_data );
    }

    next_split_id++;
    return chunks;

  }
}
