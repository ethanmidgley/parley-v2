package Client.ViewableMessage;

import Client.Gui;
import FileViewer.*;
import Message.Message;
import Message.SignalMessage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ViewableFileMessage implements ViewableMessage {

  private SignalMessage message;
  private JButton open_button;

  public ViewableFileMessage(SignalMessage message, File file) {
    this.message = message;

    this.open_button = new JButton(file.getName());
    final FileViewer viewer;
    try {
      viewer = FileViewerFactory.createFileViewer(file);
    } catch (IOException e) {
      return;
    }

    open_button.addActionListener((_event) -> {

      try {
        viewer.open();
      } catch (UnsupportedFileType e1) {
        Desktop d = Desktop.getDesktop();
        try {
          d.open(file);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } catch (IOException e1) {
        Gui.showError("Failed to open file");
      }

    });

  }

  public java.awt.Component render() {
    return open_button;
  }

  public Message underlyingMessage() {
    return this.message;
  }

}
