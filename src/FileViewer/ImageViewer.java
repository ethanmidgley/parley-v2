package FileViewer;

import org.bytedeco.javacv.CanvasFrame;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageViewer implements FileViewer {

  private final CanvasFrame frame;
  private final BufferedImage image;

  public ImageViewer(File file) throws IOException {
    this.image = ImageIO.read(file);
    this.frame = new CanvasFrame(file.getName());
    this.frame.setVisible(false);
    this.frame.setResizable(true);
    this.frame.setSize(image.getWidth(), image.getHeight());
  }

  public void open() {
    this.frame.setVisible(true);
    this.frame.showImage(this.image);
  }

}
