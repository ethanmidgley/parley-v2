package Client.Components.FilePicker;

import Client.Components.Button.ButtonFactory;
import Client.FileReceivedEvent;
import Client.Gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

public class FilePicker {



  public FilePicker(String title, FileSelectedEvent event) {

    AtomicReference<File> selectedFile = new AtomicReference<>();
    JFrame frame = Gui.makeFrame(title, 400, 200);

    JButton ok = ButtonFactory.createSkinny("Ok");
    JButton selectFile = ButtonFactory.createSkinny("Select file");

    JPanel buttons = new JPanel(new GridLayout(1, 2));
    buttons.add(selectFile);
    buttons.add(ok);

    JLabel currentFile = new JLabel("Current file: NONE");
    Border textPadding = BorderFactory.createEmptyBorder(0, 00, 10, 0);
    currentFile.setBorder(textPadding);

    JPanel mainPanel = new JPanel(new BorderLayout());
    Border padding = BorderFactory.createEmptyBorder(30, 20, 50, 20);
    mainPanel.setBorder(padding);
    mainPanel.setBackground(Gui.backColor);
    mainPanel.add(currentFile, BorderLayout.NORTH);
    mainPanel.add(buttons, BorderLayout.CENTER);

    frame.add(mainPanel);


    selectFile.addActionListener((select) -> {

      JFileChooser fileChooser = new JFileChooser();
      int returnValue = fileChooser.showOpenDialog(null);
      if (returnValue == JFileChooser.APPROVE_OPTION) {
        selectedFile.set(fileChooser.getSelectedFile());
        currentFile.setText("Current file: " + selectedFile.get().getName());
      }

    });

    ok.addActionListener((send) -> {
      frame.dispose();
      event.trigger(selectedFile.get());
    });

    frame.setVisible(true);


  }


}
