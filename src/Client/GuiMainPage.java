package Client;

import Client.Components.Button.ButtonFactory;
import Client.ViewableMessage.ViewableMessage;
import Message.Message;
import Message.TextMessage;
import Message.Type;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.net.URL;
import java.util.List;


public class GuiMainPage extends JPanel {
    public final JPanel chat;
    public JButton logoutButton;
    public JButton sendButton;
    public JButton newChatButton;
    public JButton createGameButton;
    public JButton joinGameButton;
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
        URL bannerSrc = this.getClass().getResource("/images/banner.png");
        ImageIcon bannerImage = new ImageIcon(bannerSrc, "Parley banner");
        Image scaler = bannerImage.getImage().getScaledInstance(220,80,Image.SCALE_SMOOTH);
        JLabel banner = new JLabel(new ImageIcon(scaler));

        // to add a change user button
        logoutButton = ButtonFactory.create("Logout", new Dimension(200, 50));
        headerPanel.add(logoutButton, BorderLayout.EAST);

        changeUserButton = ButtonFactory.create("Update username", new Dimension(200, 50));
        headerPanel.add(changeUserButton, BorderLayout.WEST);

        onlineUsers = new JLabel("Users online: ");
        onlineUsers.setFont(new Font("Arial", Font.PLAIN, 20));
        onlineUsers.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
        headerPanel.add(onlineUsers,BorderLayout.SOUTH);

        headerPanel.add(banner);                
        headerPanel.setBackground(gui.backColorDarkened);
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        
        fileTransferButton = ButtonFactory.createSkinny("File transfer");
        videoStreamButton = ButtonFactory.createSkinny("Video stream");
        videoCallButton = ButtonFactory.createSkinny("Video call");

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

        sendButton = ButtonFactory.create("Send", new Dimension(200, 50));

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

        newChatButton = ButtonFactory.create("New Chat");

        createGameButton = ButtonFactory.create("Create Game", new Dimension(300, 50));



        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(newChatButton, BorderLayout.NORTH);
        buttonPanel.add(createGameButton, BorderLayout.SOUTH);


        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BorderLayout());
        usersPanel.setBorder(BorderFactory.createTitledBorder("Users"));
        usersPanel.add(usersScroll, BorderLayout.CENTER);
        usersPanel.add(buttonPanel, BorderLayout.SOUTH);

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

    public void addChat(ViewableMessage message){
        chat.add(message.render());
        SwingUtilities.updateComponentTreeUI(chat);
    }

    public void addIfCurrentChat(String current_chat, ViewableMessage message){

        boolean should_show_chatroom = current_chat.equals("Chatroom") && message.underlyingMessage().getType() == Type.CHATROOM;

        if (current_chat.equals(message.underlyingMessage().getSender()) || should_show_chatroom) {
            addChat(message);
        }

    }

    public void switchChat(List<ViewableMessage> messages) {
        chat.removeAll();
        buttonsPanel.setVisible(true);
        for (ViewableMessage message : messages) {
            chat.add(message.render());
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    public JButton createNewUserButton(String username) {
        JButton button = ButtonFactory.createSkinny(username);
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