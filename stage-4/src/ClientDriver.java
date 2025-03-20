import Client.*;
import Message.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class ClientDriver {

  public static Client client;
  static File selectedFile;
  public static void main(String[] args) {

    ClientState state = new ClientState();

    Gui gui = new Gui();

    ClientDriver.initSenderView(gui, state, "Chatroom"); // creates chatroom button

    Client client = new Client((Message message) -> {
        switch (message.getType()) {

          case TEXT -> {// Check to see if we have already messaged this persons if not create a button on the side to access the conversation
            if (state.getMessages(message.getSender()) == null) {
              ClientDriver.initSenderView(gui, state, message.getSender());
            }

            state.addMessageBySender(message);

            if (state.getCurrentConversation().equals(message.getSender())) {
              // we are currently looking at the conversation so just add
              gui.mainPage.addChat(message.getSender() + ": " + message.getContent());
            }
          }

          case SIGNAL -> {
            int prompt_input = JOptionPane.showConfirmDialog(gui.mainPage, message.getSender() + " would like to send you a " + message.getContent(), "Receive " + message.getContent() + "?", JOptionPane.YES_NO_OPTION);
            System.out.println(prompt_input);
            if (prompt_input == 1){ // "Accepted: File"
              Message success_message = new Message(message.getRecipient(), message.getSender(), "Accepted : " + message.getContent(), new Date(), Type.SIGNAL_ACK);
              send_awesomely(success_message);

            } else { // "Denied"
              Message denied_message = new Message(message.getRecipient(), message.getSender(), "Denied", new Date(), Type.SIGNAL_ACK);

            }
          }

          case SIGNAL_ACK -> {
            String[] arr = message.getContent().split(":");
            System.out.println(arr);
            switch (arr[1]){
              case "File" -> {
                client.sendFile(arr[0], );
              }

              case "Video" -> {}
            }
          }

          case SERVER -> {
            System.out.println("Server message receieved");
            System.out.println(message.getContent());
          }

          case CHATROOM -> {
            state.addMessagesToChatroom(message);

            if (state.getCurrentConversation().equals("Chatroom")) {
              gui.mainPage.addChat(message.getSender() + ": " + message.getContent());
            }
          }

          case UPDATE_USERNAME -> {
            state.setUsername(message.getContent());
          }
          case ONLINE_USERS -> {
            gui.mainPage.onlineUsers.setText("Users online: " + message.getContent());
          }

          default -> {
            System.out.println("\033[2K\rError - Received incorrect message type");
          }
        }
    }, (File f) -> {});

    gui.mainPage.sendButton.addActionListener((e) -> {
      String text = gui.mainPage.chatInput.getText();
      if (!text.equals("")){
        if (state.getCurrentConversation().isEmpty()){
          gui.showError("No conversation selected");
          gui.mainPage.chatInput.setText("");
          return;
        }

        Message message = new Message(state.getUsername(), state.getCurrentConversation(), text, new Date(), Type.TEXT);
        if (message.getRecipient().equals("Chatroom")){
          message.setType(Type.CHATROOM);
        }
        client.sendMessage(message);
        state.addMessageByRecipient(message);

        gui.mainPage.addChat(state.getUsername() + ": " + text);
        gui.mainPage.chatInput.setText("");
      }
    });

    gui.mainPage.logoutButton.addActionListener((e) -> {
      System.exit(0);
  });

    gui.mainPage.changeUserButton.addActionListener((e) -> {
      String currentUsername = state.getUsername();
      String newUsername = JOptionPane.showInputDialog(gui,"Enter your new Username:"); //gets the updated username when the button is clicked through a text box
      Message mes = new Message(currentUsername, "server", newUsername, new Date(), Type.UPDATE_USERNAME);
      System.out.println(mes.getContent());
      client.sendMessage(mes);  
    });

    gui.startPage.loginButton.addActionListener((action) -> {
      if (gui.startPage.username.getText().isEmpty()) {
        gui.showError("Please enter a username");
      }
      if (gui.startPage.ipAddress.getText().isEmpty() || !isValidIPv4(gui.startPage.ipAddress.getText())) {
        gui.showError("Please enter a valid IP address");
        gui.startPage.ipAddress.setText("");
      }
      else{
        System.out.println("Logging in as " + gui.startPage.username.getText() + " to server " + gui.startPage.ipAddress.getText());

        try {
          client.connectToServer(gui.startPage.ipAddress.getText());
          if (!(gui.startPage.username.getText().equals("Chatroom"))) {
            Message prop = new Message(gui.startPage.username.getText(), "Server", gui.startPage.username.getText(), new Date(), Type.USERNAME_PROPAGATE);
            client.sendMessage(prop);
          } else {
            gui.showError("Username not allowed");
          }
          state.setUsername(gui.startPage.username.getText());
          gui.switchPanel("MainPage");
        } catch (IOException e) {
          gui.showError("Failed to connect to server");
        }
      }
    });

    gui.mainPage.newChatButton.addActionListener((e) -> {
      String new_user = JOptionPane.showInputDialog(gui.mainPage, "Who do you want to message?", "New Chat", JOptionPane.QUESTION_MESSAGE);

      // Add them to the user list? and when they do an onclick change the state to the username
      if (state.getMessages(new_user) == null) {
        initSenderView(gui, state, new_user);
      }
    });

    gui.mainPage.fileTransferButton.addActionListener((e) -> {
      selectedFile = null;
      JFrame frame = new JFrame();
      frame.setTitle("File transfer");
      frame.setSize(400, 200);
      frame.setLocationRelativeTo(null);

      JButton sendFile = new JButton("Send file");
      sendFile.setFont(new Font("Arial", Font.BOLD, 15));
      JButton selectFile = new JButton("Select file");
      selectFile.setFont(new Font("Arial", Font.BOLD, 15));

      JPanel buttons = new JPanel(new GridLayout(1,2));
      buttons.add(selectFile);
      buttons.add(sendFile);

      JLabel currentFile = new JLabel("Current file: NONE");
      Border textPadding = BorderFactory.createEmptyBorder(0, 00, 10, 0);
      currentFile.setBorder(textPadding);

      JPanel mainPanel = new JPanel(new BorderLayout());
      Border padding = BorderFactory.createEmptyBorder(30, 20, 50, 20);
      mainPanel.setBorder(padding);
      mainPanel.setBackground(gui.backColor);
      mainPanel.add(currentFile, BorderLayout.NORTH);
      mainPanel.add(buttons, BorderLayout.CENTER);

      frame.add(mainPanel);
      frame.setVisible(true);


      selectFile.addActionListener((select) -> {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          selectedFile = fileChooser.getSelectedFile();
          currentFile.setText("Current file: " + selectedFile.getName());
        }
      });

      sendFile.addActionListener((send) -> {
        if (selectedFile != null){
          JOptionPane.showMessageDialog(null, "Sending: " + selectedFile.getName() , "File transfer", JOptionPane.INFORMATION_MESSAGE);
          frame.dispose();
          gui.mainPage.addChat(gui.startPage.username.getText() + " sent a file: " + selectedFile.getName());
          
          JButton openFile = new JButton(selectedFile.getName());
          File file = selectedFile;
          openFile.addActionListener((Test) -> {
            try {
              Message file_req = new Message(state.getUsername(), state.getCurrentConversation(), "File", new Date(), Type.SIGNAL);
              client.sendMessage(file_req);
              java.awt.Desktop.getDesktop().open(file);
            } catch (IOException ioe) {
              gui.showError("Failed to open file");
            }
          });
          gui.mainPage.chat.add(openFile);
        }
      });
    });

    gui.mainPage.videoStreamButton.addActionListener((e) -> {
      
    });

    gui.mainPage.videoCallButton.addActionListener((e) -> {
      
    });
  }

  public static boolean isValidIPv4(String ip) {
    // Step 1: Separate the given string into an array of strings using the dot as delimiter
    String[] parts = ip.split("\\.");

    // Step 2: Check if there are exactly 4 parts
    if (parts.length != 4) {
      return false;
    }

    // Step 3: Check each part for valid number
    for (String part : parts) {
      try {
        // Step 4: Convert each part into a number
        int num = Integer.parseInt(part);

        // Step 5: Check whether the number lies in between 0 to 255
        if (num < 0 || num > 255) {
          return false;
        }
      } catch (NumberFormatException e) {
        // If parsing fails, it's not a valid number
        return false;
      }
    }
    // If all checks passed, return true
    return true;
  }

  public static void initSenderView(Gui gui, ClientState state, String sender_name){
    JButton chat = gui.mainPage.createNewUserButton(sender_name);
    state.initialiseConversation(sender_name);

    chat.addActionListener((action) -> {
      state.setCurrentConversation(sender_name);
      gui.mainPage.updateButtons(chat);
      gui.mainPage.switchChat(state.getMessages(sender_name));
    });
    gui.mainPage.users.revalidate();
  }
}
