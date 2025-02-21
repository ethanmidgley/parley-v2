import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

public class Gui extends JFrame {

    private static JPanel chat;
    private JPanel mainPanel;


    public Gui() {
        setTitle("Parley");
        setSize(1400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel headerPanel = new JPanel(new GridBagLayout());
        ImageIcon banners = new ImageIcon("assets/images/banner.png", "Parley banner");
        Image scaler = banners.getImage().getScaledInstance(320,120,Image.SCALE_SMOOTH);
        JLabel banner = new JLabel(new ImageIcon(scaler));

        headerPanel.add(banner);
        headerPanel.setBackground(new Color(121, 189, 232));
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.black));



        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(new Color(121, 189, 232));
        Border padding = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        
        chat =  new JPanel();
        chat.setLayout(new BoxLayout(chat, BoxLayout.Y_AXIS));
        for (int i = 0; i < 10; i++){
            JLabel chatLine = new JLabel("Hello"+i);
            chat.add(chatLine);
        } 
        
        JScrollPane chatScroll = new JScrollPane(chat);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScroll.setBorder(BorderFactory.createTitledBorder("Chat"));
        chatScroll.setPreferredSize(new Dimension(1000, 600));

        JPanel users =  new JPanel();
        users.setLayout(new BoxLayout(users, BoxLayout.Y_AXIS));
        for (int i = 0; i < 10; i++){
            JButton button = new JButton("<html><a href=''>User " + (i + 1) +  "</a><html>");
            users.add(button);
            users.add(Box.createVerticalStrut(10));
        } 

        JScrollPane usersScroll = new JScrollPane(users);
        usersScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        usersScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        usersScroll.setBorder(BorderFactory.createTitledBorder("Users"));
        usersScroll.setPreferredSize(new Dimension(300, 600));


        bodyPanel.add(chatScroll, BorderLayout.CENTER);
        bodyPanel.add(usersScroll, BorderLayout.WEST);
        bodyPanel.setBorder(padding);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(121, 189, 232));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(bodyPanel, BorderLayout.CENTER);  

        add(mainPanel);
        setVisible(true);
    }


    public void addChat(String message){
        JLabel chatLine = new JLabel(message);
        chat.add(chatLine);
        SwingUtilities.updateComponentTreeUI(mainPanel);
    }

    public static void main(String[] args) {
        Gui gui = new Gui();
        
        gui.setVisible(true);
    }
}
