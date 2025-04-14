package Client;

import Message.Message;
import Message.Type;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;


public class GuiMainPage extends JPanel {
    public final JPanel chat;
    public JButton logoutButton;
    public JButton sendButton;
    public JButton newChatButton;
    public JTextField chatInput;
    public JPanel users;
    public JButton changeUserButton;
    public JLabel onlineUsers;
    public JPanel buttonsPanel;
    public JButton fileTransferButton;
    public JButton videoStreamButton;
    public JButton videoCallButton;


    public GuiMainPage(Gui gui) {
        setLayout(new BorderLayout());


        JPanel headerPanel = new JPanel(new BorderLayout());
        ImageIcon bannerImage = new ImageIcon("../assets/images/banner.png", "Parley banner");
        Image scaler = bannerImage.getImage().getScaledInstance(220,80,Image.SCALE_SMOOTH);
        JLabel banner = new JLabel(new ImageIcon(scaler));

        // to add a change user button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(logoutButton, BorderLayout.EAST);

        changeUserButton = new JButton("Update Username");
        changeUserButton.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(changeUserButton, BorderLayout.WEST);

        onlineUsers = new JLabel("Users online: ");
        onlineUsers.setFont(new Font("Arial", Font.BOLD, 20));
        onlineUsers.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        headerPanel.add(onlineUsers,BorderLayout.SOUTH);

        headerPanel.add(banner);                
        headerPanel.setBackground(gui.backColorDarkened);
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        
        fileTransferButton = new JButton("File transfer");
        fileTransferButton.setFont(new Font("Arial", Font.BOLD, 15));
        videoStreamButton = new JButton("Video stream");
        videoStreamButton.setFont(new Font("Arial", Font.BOLD, 15));
        videoCallButton = new JButton("Video call");
        videoCallButton.setFont(new Font("Arial", Font.BOLD, 15));

        buttonsPanel = new JPanel(new GridLayout());
        buttonsPanel.add(fileTransferButton);
        buttonsPanel.add(videoStreamButton);
        buttonsPanel.add(videoCallButton);
        buttonsPanel.setVisible(false);

        
        chat =  new JPanel();
        chat.setLayout(new BoxLayout(chat, BoxLayout.Y_AXIS));

        JScrollPane chatScroll = new JScrollPane(chat);
        chatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chatScroll.setPreferredSize(new Dimension(1000, 600));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 30));
        sendButton.setPreferredSize(new Dimension(200, 50));

        chatInput = new JTextField();
        chatInput.setPreferredSize(new Dimension(1000, 50));
        chatInput.setFont(new Font("Arial", Font.PLAIN, 20));

        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputPanel.add(chatInput, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        this.users =  new JPanel();
        users.setLayout(new GridLayout(20,1));

        JScrollPane usersScroll = new JScrollPane(users);
        usersScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        usersScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        usersScroll.setPreferredSize(new Dimension(300, 500));

        newChatButton = new JButton("New Chat");
        newChatButton.setFont(new Font("Arial", Font.BOLD, 30));
        newChatButton.setPreferredSize(new Dimension(300, 50));

        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BorderLayout());
        usersPanel.setBorder(BorderFactory.createTitledBorder("Users"));
        usersPanel.add(usersScroll, BorderLayout.CENTER);
        usersPanel.add(newChatButton, BorderLayout.SOUTH);

        JPanel mainChatPanel = new JPanel(new BorderLayout());
        mainChatPanel.setBorder(BorderFactory.createTitledBorder("Chat"));

        mainChatPanel.add(chatPanel, BorderLayout.CENTER);
        mainChatPanel.add(buttonsPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel(new BorderLayout());
        bodyPanel.setBackground(gui.backColor);
        Border padding = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        bodyPanel.setBorder(padding);

        bodyPanel.add(mainChatPanel, BorderLayout.CENTER);
        bodyPanel.add(usersPanel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
    }

    public void addChat(String message){
        JLabel chatLine = new JLabel(message);
        chatLine.setFont(new Font("Arial", Font.PLAIN, 20));
        chat.add(chatLine);
        SwingUtilities.updateComponentTreeUI(chat);
    }

    public void switchChat(java.util.List<Message> messages) {
        chat.removeAll();
        buttonsPanel.setVisible(true);
        for (Message message : messages) {
            if (message.getType() == Type.CHATROOM){
                buttonsPanel.setVisible(false);
            }
            JLabel chatLine = new JLabel(message.getSender() + ": " + message.getContent());
            chatLine.setFont(new Font("Arial", Font.PLAIN, 20));
            chat.add(chatLine);
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    public JButton createNewUserButton(String username) {
        JButton button = new JButton(username);
        button.setFont(new Font("Arial", Font.PLAIN, 20));

        this.users.add(button);
        return button;
    }

    public void updateButtons(JButton currentConvo){
        Component[] buttons = users.getComponents();
        users.removeAll();
        currentConvo.setFont(new Font("Arial", Font.BOLD, 20));
        users.add(currentConvo);
        JButton spacer = new JButton("");
        spacer.setVisible(false);
        users.add(spacer);
        for (Component button : buttons){
            if (button != currentConvo && button.isVisible() == true){
                button.setFont(new Font("Arial", Font.PLAIN, 20));
                users.add(button);
            }
        }
    }
}