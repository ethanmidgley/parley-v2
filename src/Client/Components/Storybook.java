package Client.Components;

import Client.Gui;
import Games.Blackjack.utils.Card;
import Games.Blackjack.utils.Deck;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Storybook {
  public static void main(String[] args) {

    JFrame f = Gui.makeFrame("Storybook", 800, 600);
    // THis is good for your hand & dealer
//    f.setLayout(new FlowLayout(FlowLayout.CENTER, -50,0));

    // Maybe this is better actually

    Deck d = new Deck();
    d.shuffle();

    List<Card> c = new ArrayList<>();

    for (int i = 0; i < 5; i++) {
//      JPanel c = new PlayingCard(d.deal(), new Dimension(90, 128), i != 4);
      c.add(d.deal());
    }

    f.add(new HandComponent(c));

    f.setVisible(true);

  }


}
