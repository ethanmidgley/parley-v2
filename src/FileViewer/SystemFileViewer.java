package FileViewer;

import java.io.File;
import java.io.IOException;

public class SystemFileViewer implements FileViewer {
  private File file;

  public SystemFileViewer(File file) {
    this.file = file;
  }

  public void open() throws IOException {
    java.awt.Desktop.getDesktop().open(file);
  }
}
