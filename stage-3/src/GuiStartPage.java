import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

class GuiStartPage extends JPanel {
    private static JTextField username;

    JButton loginButton;
    public GuiStartPage(Gui gui) {
        setLayout(new BorderLayout());
        setBackground(gui.backColor);
        Border mainPadding = BorderFactory.createEmptyBorder(200, 150, 150, 200);
        setBorder(mainPadding);

        JPanel bodyPanel = new JPanel(new BorderLayout());


        JLabel welcome = new JLabel("Welcome to Parley");
        welcome.setFont(new Font("Arial", Font.BOLD, 60));
        welcome.setHorizontalAlignment(SwingConstants.CENTER);

        username = new JTextField();
        username.setFont(new Font("Arial", Font.PLAIN, 20));
        username.setBorder(BorderFactory.createTitledBorder("Enter username"));

        this.loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel loginPanel = new JPanel(new BorderLayout());
        Border loginPadding = BorderFactory.createEmptyBorder(100, 20, 150, 20);
        loginPanel.setBorder(loginPadding);
        loginPanel.setBackground(gui.backColorDarkened);
        loginPanel.add(username, BorderLayout.CENTER);
        loginPanel.add(loginButton, BorderLayout.EAST);


        bodyPanel.setBackground(gui.backColorDarkened);
        Border bodyPadding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        bodyPanel.setBorder(bodyPadding);

        bodyPanel.add(welcome, BorderLayout.NORTH);
        bodyPanel.add(loginPanel, BorderLayout.CENTER);

        add(bodyPanel, BorderLayout.CENTER);

        loginButton.addActionListener((e) -> {
            if (username.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else{
                System.out.println("Logging in as " + username.getText());
                gui.switchPanel("MainPage");
            }
        });
    }

    public static void clearFields() {
        username.setText("");
    }
}