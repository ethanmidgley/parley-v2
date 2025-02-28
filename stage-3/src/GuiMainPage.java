import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;


class GuiMainPage extends JPanel {
    private final JPanel chat;

    public GuiMainPage(Gui gui) {
        setLayout(new BorderLayout());


        JPanel headerPanel = new JPanel(new BorderLayout());
        ImageIcon banners = new ImageIcon("assets/images/banner.png", "Parley banner");
        Image scaler = banners.getImage().getScaledInstance(320,120,Image.SCALE_SMOOTH);
        JLabel banner = new JLabel(new ImageIcon(scaler));

        headerPanel.add(banner);
        headerPanel.setBackground(gui.backColor);
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.black));



        chat =  new JPanel();
        chat.setLayout(new BoxLayout(chat, BoxLayout.Y_AXIS));

        JScrollPane chatScroll = new JScrollPane(chat);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScroll.setPreferredSize(new Dimension(1000, 600));

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 30));
        sendButton.setPreferredSize(new Dimension(200, 50));

        JTextField chatInput = new JTextField();
        chatInput.setPreferredSize(new Dimension(1000, 50));
        chatInput.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Chat"));
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);



        JPanel users =  new JPanel();
        users.setLayout(new GridLayout(100,1));
        for (int i = 0; i < 10; i++){
            JButton button = new JButton("User " + (i + 1));
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setPreferredSize(new Dimension(1, 40));
            users.add(button);
        } 

        JScrollPane usersScroll = new JScrollPane(users);
        usersScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        usersScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        usersScroll.setPreferredSize(new Dimension(300, 500));

        JButton newChatButton = new JButton("New Chat");
        newChatButton.setFont(new Font("Arial", Font.BOLD, 30));
        newChatButton.setPreferredSize(new Dimension(300, 50));

        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BorderLayout());
        usersPanel.setBorder(BorderFactory.createTitledBorder("Users"));
        usersPanel.add(usersScroll, BorderLayout.CENTER);
        usersPanel.add(newChatButton, BorderLayout.SOUTH);



        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(gui.backColor);
        Border padding = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        bodyPanel.setBorder(padding);

        bodyPanel.add(chatPanel, BorderLayout.CENTER);
        bodyPanel.add(usersPanel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);


        sendButton.addActionListener((e) -> {
            if (!chatInput.getText().equals("")){
                System.out.println("Sending message: " + chatInput.getText());
                addChat("You: " + chatInput.getText());
                chatInput.setText("");
            }
        });

        newChatButton.addActionListener((e) -> {
            System.out.println("Logging out");
            GuiStartPage.clearFields();
            gui.switchPanel("StartPage");
        });
    }

    public void addChat(String message){
        JLabel chatLine = new JLabel(message);
        chat.add(chatLine);
        SwingUtilities.updateComponentTreeUI(this);
    }
}