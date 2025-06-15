package Client.Components.Button;

import javax.swing.*;
import java.awt.*;

public class ButtonFactory {

  public static JButton create(String text) {

    JButton btn =  new JButton(text);
    btn.setFont(new Font("Arial", Font.PLAIN, 20));
    btn.setPreferredSize(new Dimension(300, 50));

    return btn;
  }

  public static JButton createSkinny(String text) {
    JButton btn =  new JButton(text);
    btn.setFont(new Font("Arial", Font.PLAIN, 20));
    return btn;
  }

  public static JButton create(String text, Font font) {

    JButton btn =  new JButton(text);
    btn.setFont(font);
    btn.setPreferredSize(new Dimension(300, 50));
    return btn;

  }

  public static JButton create(String text, Dimension dimension) {

    JButton btn =  new JButton(text);
    btn.setFont(new Font("Arial", Font.PLAIN, 20));
    btn.setPreferredSize(dimension);
    return btn;

  }

  public static JButton create(String text, Font font, Dimension dimension) {

    JButton btn =  new JButton(text);
    btn.setFont(font);
    btn.setPreferredSize(dimension);
    return btn;

  }



}
