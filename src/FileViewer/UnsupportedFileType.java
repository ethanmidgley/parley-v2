package FileViewer;

public class UnsupportedFileType extends RuntimeException {
  public UnsupportedFileType(String message) {
    super(message);
  }
}
