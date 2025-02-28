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
        setTitle("Parley");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel headerPanel = new JPanel(new GridBagLayout());
        ImageIcon banners = new ImageIcon("assets/images/banner.png", "Parley banner");
        Image scaler = banners.getImage().getScaledInstance(320,120,Image.SCALE_SMOOTH);
        JLabel banner = new JLabel(new ImageIcon(scaler));

        headerPanel.add(banner);
        headerPanel.setBackground(backColor);
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPage = new GuiMainPage(this);
        startPage = new GuiStartPage(this);

        mainPanel.add(startPage, "StartPage");
        mainPanel.add(mainPage, "MainPage");


        add(mainPanel);
        setVisible(true);
    }

    public void switchPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }


    public static void main(String[] args) {
        new Gui();
    }
}
