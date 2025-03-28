package FileViewer;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class FileViewerFactory {

  private static final Set<String> SUPPORTED_VIDEO_EXTENSIONS = Set.of("mp4");
  private static final Set<String> SUPPORTED_IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg", "gif");

  public static FileViewer createFileViewer(File file) throws UnsupportedFileType, IOException {

    int i = file.getName().lastIndexOf('.');
    if (i == -1) {
      throw new UnsupportedFileType("file without an extension");
    }

    String extension = file.getName().substring(i + 1).toLowerCase();

    if (SUPPORTED_VIDEO_EXTENSIONS.contains(extension)) {
      return new VideoPlayer(file);
    } else if (SUPPORTED_IMAGE_EXTENSIONS.contains(extension)) {
      return new ImageViewer(file);
    }

    return new SystemFileViewer(file);


  }

}