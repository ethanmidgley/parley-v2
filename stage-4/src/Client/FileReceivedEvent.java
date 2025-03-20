package Client;

import java.io.File;

public interface FileReceivedEvent {
  void trigger(File file);
}
