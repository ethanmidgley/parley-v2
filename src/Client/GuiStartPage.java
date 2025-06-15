package Client;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.*;

public class GuiStartPage extends JPanel {

    public JButton loginButton;
    public JTextField username;
    public JTextField ipAddress;

    public GuiStartPage(Gui gui) {
        setLayout(new BorderLayout());
        setBackground(gui.backColor);
        Border mainPadding = BorderFactory.createEmptyBorder(150, 150, 150, 200);
        setBorder(mainPadding);

        URL bannerSrc = this.getClass().getResource("/images/banner.png");

        ImageIcon bannerImage = new ImageIcon(bannerSrc, "Parley banner");
        Image scaler = bannerImage.getImage().getScaledInstance(340,120,Image.SCALE_SMOOTH);
        JLabel banner = new JLabel(new ImageIcon(scaler));


        username = new JTextField();
        username.setFont(new Font("Arial", Font.PLAIN, 20));
        username.setBorder(BorderFactory.createTitledBorder("Enter your username"));

        ipAddress = new JTextField();
        ipAddress.setFont(new Font("Arial", Font.PLAIN, 20));
        ipAddress.setBorder(BorderFactory.createTitledBorder("Enter Server IP address"));

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 30));


        JPanel loginPanel = new JPanel(new GridLayout(3,1,0,10));
        Border loginPadding = BorderFactory.createEmptyBorder(60, 20, 60, 20);
        loginPanel.setBorder(loginPadding);
        loginPanel.setBackground(gui.backColorDarkened);
        loginPanel.add(username);
        loginPanel.add(ipAddress);
        loginPanel.add(loginButton);


        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(gui.backColorDarkened);
        bodyPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        bodyPanel.add(banner, BorderLayout.NORTH);
        bodyPanel.add(loginPanel, BorderLayout.CENTER);

        add(bodyPanel, BorderLayout.CENTER);
    }

    public void clearFields() {
        username.setText("");
        ipAddress.setText("");
    }
}