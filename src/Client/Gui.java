package Client;

import java.awt.*;
import javax.swing.*;


public class Gui extends JFrame {

    private final JPanel mainPanel;
    private CardLayout cardLayout;
    public Color backColor = new Color(121, 189, 232); 
    public Color backColorDarkened = new Color(101, 169, 212);
    public GuiMainPage mainPage;
    public GuiStartPage startPage;


    public Gui() {
        super();
        setTitle("Parley");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon logo = new ImageIcon("../assets/images/logo.png");
        super.setIconImage(logo.getImage());


        JPanel headerPanel = new JPanel(new GridBagLayout());
        ImageIcon banners = new ImageIcon("../assets/images/banner.png", "Parley banner");
        Image scaler = banners.getImage().getScaledInstance(320,120,Image.SCALE_SMOOTH);
        JLabel banner = new JLabel(new ImageIcon(scaler));

        headerPanel.add(banner);
        headerPanel.setBackground(backColor);
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        this.mainPage = new GuiMainPage(this);
        this.startPage = new GuiStartPage(this);

        mainPanel.add(startPage, "StartPage");
        mainPanel.add(mainPage, "MainPage");


        add(mainPanel);
        setVisible(true);
    }

    public void switchPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public void showError(String errorMessage){
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public JFrame makeFrame(String name, int width, int height){
        JFrame frame = new JFrame(name);
        frame.setSize(width,height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
        return frame;
    }
}
