package Client;

import FileViewer.FileViewerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Gui extends JFrame {

    private final JPanel mainPanel;
    private CardLayout cardLayout;
    public static final Color backColor = new Color(121, 189, 232);
    public static final Color backColorDarkened = new Color(101, 169, 212);
    public GuiMainPage mainPage;
    public GuiStartPage startPage;


    public Gui() {
        super();
        setTitle("Parley");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon logo = new ImageIcon(this.getClass().getResource("/images/logo.png"));
        super.setIconImage(logo.getImage());

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

    public static void showError(String errorMessage){
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(String successMessage){
        JOptionPane.showMessageDialog(null, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
    }


    public static JFrame makeFrame(String name, int width, int height){

        JFrame frame = new JFrame(name);
        frame.setSize(width,height);
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
        return frame;
    }
}
