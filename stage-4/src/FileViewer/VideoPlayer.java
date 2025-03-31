package FileViewer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class VideoPlayer extends JFrame implements FileViewer {

  private MediaPlayer mediaPlayer;

  public VideoPlayer(File file) {

    setTitle("Video Player");
    setSize(800, 600);

    JPanel mainPanel = new JPanel(new BorderLayout());


    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        if (mediaPlayer != null) {
          mediaPlayer.stop();
          setVisible(false);
        }
      }
    });

    JFXPanel jfxPanel = new JFXPanel();

    mainPanel.add(jfxPanel, BorderLayout.CENTER);
    mainPanel.add(createControlPanel(), BorderLayout.SOUTH);
    add(mainPanel);

    Media media = new Media(file.toURI().toString());
    this.mediaPlayer = new MediaPlayer(media);

    Platform.runLater(() -> {
      if (!file.exists()) {
        JOptionPane.showMessageDialog(this, "Video file not found.");
        return;
      }

      MediaView mediaView = new MediaView(mediaPlayer);
      mediaView.setFitHeight(600);
      mediaView.setFitWidth(800);
      mediaView.setPreserveRatio(true);

      jfxPanel.setScene(new Scene(new javafx.scene.Group(mediaView)));
    });

    setResizable(false);
    setVisible(false);
  }

  private JPanel createControlPanel() {
    JPanel controlPanel = new JPanel();
    JButton playButton = new JButton("Play");
    JButton pauseButton = new JButton("Pause");
    JButton stopButton = new JButton("Stop");
    JButton rewindButton = new JButton("<< 10s");
    JButton skipButton = new JButton("10s >>");

    playButton.addActionListener(e -> Platform.runLater(() -> mediaPlayer.play()));
    pauseButton.addActionListener(e -> Platform.runLater(() -> mediaPlayer.pause()));
    stopButton.addActionListener(e -> Platform.runLater(() -> mediaPlayer.stop()));
    rewindButton.addActionListener(e -> Platform.runLater(() -> mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(javafx.util.Duration.seconds(10)))));
    skipButton.addActionListener(e -> Platform.runLater(() -> mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(10)))));

    controlPanel.add(rewindButton);
    controlPanel.add(playButton);
    controlPanel.add(pauseButton);
    controlPanel.add(stopButton);
    controlPanel.add(skipButton);

    return controlPanel;
  }

  public void open() {
    setVisible(true);
    mediaPlayer.play();
  }

}
