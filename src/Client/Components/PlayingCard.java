package Client.Components;

import Games.Blackjack.utils.Card;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PlayingCard extends JPanel {

  public PlayingCard(Card card, Dimension size, boolean show_shadow) {

    URL imgSrc = this.getClass().getResource("/cards/" + card.getFilename());
    setSize(size);
    setLayout(new BorderLayout());
    ImageIcon cardImage = new ImageIcon(imgSrc, "Playing card");
    Image scaler = cardImage.getImage().getScaledInstance(size.width ,size.height,Image.SCALE_SMOOTH);
    JLabel banner = new JLabel(new ImageIcon(scaler));
    banner.setBorder(BorderFactory.createEmptyBorder());
    banner.setOpaque(true);

    setOpaque(false);
    JPanel shadow = new JPanel();
    shadow.setBackground(new Color(0,(float)0.36,(float)0.5,(float)0.1));
    banner.setBackground(new Color(0,(float)0.36,(float)0.5,(float)0.1));
    shadow.setSize(1, size.height);
    shadow.setMaximumSize(new Dimension(1, size.height));
    shadow.setVisible(show_shadow);
    add(banner, BorderLayout.CENTER);
    add(shadow, BorderLayout.EAST);

  }

}
