import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

class GuiStartPage extends JPanel {

    public JButton loginButton;
    public JTextField username;
    public JTextField ipAddress;

    public GuiStartPage(Gui gui) {
        setLayout(new BorderLayout());
        setBackground(gui.backColor);
        Border mainPadding = BorderFactory.createEmptyBorder(200, 150, 150, 200);
        setBorder(mainPadding);

        JPanel bodyPanel = new JPanel(new BorderLayout());


        ipAddress = new JTextField();
        ipAddress.setFont(new Font("Arial", Font.PLAIN, 20));
        ipAddress.setBorder(BorderFactory.createTitledBorder("Enter IP address"));


        JLabel welcome = new JLabel("Welcome to Parley");
        welcome.setFont(new Font("Arial", Font.BOLD, 60));
        welcome.setHorizontalAlignment(SwingConstants.CENTER);

        this.username = new JTextField();
        username.setFont(new Font("Arial", Font.PLAIN, 20));
        username.setBorder(BorderFactory.createTitledBorder("Enter username"));

        this.loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel loginPanel = new JPanel(new BorderLayout());
        Border loginPadding = BorderFactory.createEmptyBorder(100, 20, 100, 20);
        loginPanel.setBorder(loginPadding);
        loginPanel.setBackground(gui.backColorDarkened);
        loginPanel.add(username, BorderLayout.CENTER);
        loginPanel.add(loginButton, BorderLayout.EAST);


        bodyPanel.setBackground(gui.backColorDarkened);
        Border bodyPadding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        bodyPanel.setBorder(bodyPadding);

        bodyPanel.add(welcome, BorderLayout.NORTH);
        bodyPanel.add(loginPanel, BorderLayout.CENTER);
        
        add(ipAddress, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
    }

    public void clearFields() {
        username.setText("");
    }
}