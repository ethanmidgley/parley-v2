package Client.Components;


import Games.Blackjack.utils.Card;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HandComponent extends JPanel {

  public HandComponent(List<Card> cards) {

    setLayout(new FlowLayout(FlowLayout.CENTER, -75, 0));
    setPreferredSize(new Dimension(250, 128));
    setOpaque(false);

    for (int i = 0; i < cards.size(); i ++) {
      JPanel c = new PlayingCard(cards.get(i), new Dimension(90, 128), i != cards.size()-1);
      add(c);
    }

    setVisible(true);

  }
}
