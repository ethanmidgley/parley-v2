package Client.Components.FilePicker;

import java.io.File;

public interface FileSelectedEvent {
  void trigger(File file);
}

