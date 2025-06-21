package Client.Components.GamePicker;

import Client.Components.Button.ButtonFactory;
import Client.Gui;
import Message.Game.GameType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GamePicker {

  public static void Pick(PickEvent event) {

    JFrame frame = Gui.makeFrame("Pick game", 600, 400);

    WindowAdapter cancelListener = new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        event.onPick(null);
      }
    };


    JPanel gamesPanel =  new JPanel();

    gamesPanel.setBackground(Gui.backColor);
    gamesPanel.setLayout(new BorderLayout());
    gamesPanel.setBorder(new EmptyBorder(10 , 10, 10, 10));

    JLabel text = new JLabel("Pick a game");


    JPanel optionsPanel = new JPanel();
    optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
    JScrollPane gamesScroll = new JScrollPane(optionsPanel);
    gamesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    gamesPanel.add(text, BorderLayout.NORTH);
    gamesPanel.add(gamesScroll, BorderLayout.CENTER);

    for (GameType t : GameType.values()) {
      String pretty_print = t.name().substring(0, 1) + t.name().substring(1).toLowerCase();
      JButton btn = ButtonFactory.createSkinny(pretty_print);

      btn.addActionListener((e) -> {
        event.onPick(t);
        frame.removeWindowListener(cancelListener);
        frame.dispose();
      });
      optionsPanel.add(btn);
    }


    frame.add(gamesPanel);
    frame.addWindowListener(cancelListener);

    frame.setVisible(true);

  }

}
