import Client.*;
import FileViewer.*;
import Message.*;
import VideoStreamer.FileReceiver;
import VideoStreamer.FileStreamer;
import VideoStreamer.WebcamStreamerReceiver;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import javax.swing.border.Border;

public class ClientDriver {

  public static Client client;
  static File selectedFile;
  static File selectedStreamFile;
  static HashMap<String, JButton> buttonMap = new HashMap<String, JButton>();
  public static void main(String[] args) {

    ClientState state = new ClientState();

    Gui gui = new Gui();

    ClientDriver.initSenderView(gui, state, "Chatroom"); // creates chatroom button

    Client client = new Client();

    client.bindMessageReceive((Message message) -> {
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

        case SIGNAL -> { // this is when the user receives a handshake request, it will ask if they want to allow their peer to receive their ip through the server
          int prompt_input = JOptionPane.showConfirmDialog(gui.mainPage, message.getSender() + " would like to send you a " + message.getContent(), "Receive " + message.getContent() + "?", JOptionPane.YES_NO_OPTION);


          if (state.getMessages(message.getSender()) == null) {
            JButton button = new JButton();
            button = ClientDriver.initSenderView(gui, state, message.getSender());
            gui.mainPage.updateButtons(button);
          }

          if (!(state.getCurrentConversation().equals(message.getSender()))) {
            gui.mainPage.switchChat(state.getMessages(message.getSender()));
            state.setCurrentConversation(message.getSender());
            gui.mainPage.updateButtons(buttonMap.get(message.getSender()));
          }

          if (prompt_input == 0) { // "Accepted: File"
            //TODO: handle exceptions better
            switch (message.getContent().toLowerCase()) {
              case "stream" -> { //video stream
                try {
                  FileReceiver fr = new FileReceiver() ;
                } catch (LineUnavailableException e) {
                  e.printStackTrace();
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
              case "webcam" -> { //webcam stream
                try {
                  //pass null to wait for other ends connection to come through
                  //receiver end
                  // don't know how we're going to terminate wr
                  WebcamStreamerReceiver wr = new WebcamStreamerReceiver(null);
                  wr.start();
                } catch(IOException e) {
                  e.printStackTrace();
                } catch (LineUnavailableException e) {
                  throw new RuntimeException(e);
                }
              }
            }

            Message success_message = new Message(message.getRecipient(), message.getSender(), "Accepted : " + message.getContent(), new Date(), Type.SIGNAL_ACK);
            client.sendMessage(success_message);

          } else { // "Denied"
            Message denied_message = new Message(message.getRecipient(), message.getSender(), "Denied", new Date(), Type.SIGNAL_ACK);
            client.sendMessage(denied_message);
          }
        }

        case SIGNAL_ACK -> { // this is when a user receives a handshake response from the server, carrying either a denied message from the other user or their ip and the type of connection they want to make
          System.out.println(message);
          if (!(message.getContent().equals("Denied"))) { // if the other user didn't deny their request, if they did, it will change to a server message, so we don't need to handle that here
            String[] arr = message.getContent().split(":"); // just splitting the ip from the type of connection
            InetAddress peer_address = null;
            try {
              peer_address = InetAddress.getByName(arr[0]);
            } catch (UnknownHostException e) {
              System.out.println("Error: No Ip Found");
            }
            //TODO: handle exceptions better
            System.out.println(arr[1].trim().toLowerCase());
            switch (arr[1].trim().toLowerCase()) { // arr[1] contains the type of connection, be it file, video...
              case "file" -> {
                Message server_message = new Message(message.getRecipient(), message.getSender(), "file", new Date(), Type.SERVER);
                client.sendMessage(server_message);
                client.sendFile(peer_address, selectedFile);
              }
              case "stream" -> {
                try {
                  Message server_message = new Message(message.getRecipient(), message.getSender(), "file stream", new Date(), Type.SERVER);
                  client.sendMessage(server_message);
                  FileStreamer fs = new FileStreamer(peer_address,selectedStreamFile);
                  fs.start();
                  System.out.println("started the file streamer");
                } catch(IOException e) {
                  e.printStackTrace();
                } catch (LineUnavailableException e) {
                  e.printStackTrace();
                }
              }
              case "webcam" -> {
                try {
                  Message server_message = new Message(message.getRecipient(), message.getSender(), "video call", new Date(), Type.SERVER);
                  client.sendMessage(server_message);
                  WebcamStreamerReceiver ws = new WebcamStreamerReceiver(peer_address);
                  ws.start();
                } catch (IOException | LineUnavailableException e) {
                  e.printStackTrace();
                }
              }
            }
          }
        }

        case SERVER -> {
          System.out.println("Server message received");
          if (message.getContent().equals("Error - Name already taken")) {
            gui.startPage.clearFields();
            gui.switchPanel("StartPage");
            gui.showError("Username already taken");
            return;
          }
          state.addMessageBySender(message);
          if (state.getCurrentConversation().equals(message.getSender())) {
            gui.mainPage.addChat(message.getSender() + ": " + message.getContent());
          }
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
    });

    client.bindFileReceive((File file) -> {
      System.out.println("File received");
      gui.mainPage.addChat("Received a file: " + file.getName());
      JButton openReceivedFile = new JButton(file.getName());

      openReceivedFile.addActionListener((Test) -> {
        try {
          FileViewer fileViewer = FileViewerFactory.createFileViewer(file);
          fileViewer.open();
        } catch (UnsupportedFileType e1) {
          gui.showError("Unsupported file type");
        } catch (IOException e1) {
          gui.showError("Failed to open file");
        }
      });
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      gui.mainPage.chat.add(openReceivedFile);
      gui.mainPage.chat.revalidate();
    });

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
      if (newUsername.length() > 25) {
        gui.showError("Username too long");
        return;
      }
      if (newUsername == null || newUsername.isEmpty()) {
        gui.showError("Please enter a username");
        return;
      }
      Message mes = new Message(currentUsername, "server", newUsername, new Date(), Type.UPDATE_USERNAME);
      client.sendMessage(mes);
      Message update_username_chatroom = new Message("Server", "Chatroom", currentUsername + " changed their name to " + newUsername, new Date(), Type.CHATROOM);
      client.sendMessage(update_username_chatroom);
    });

    gui.startPage.loginButton.addActionListener((action) -> {
      if (gui.startPage.username.getText().isEmpty()) {
        gui.showError("Please enter a username");
        return;
      }
      if (gui.startPage.username.getText().length() > 25) {
        gui.showError("Username too long");
        gui.startPage.username.setText("");
        return;
      }
      if (gui.startPage.ipAddress.getText().isEmpty() || !isValidIPv4(gui.startPage.ipAddress.getText())) {
        gui.showError("Please enter a valid IP address");
        gui.startPage.ipAddress.setText("");
        return;
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
            return;
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

      if (new_user == null || new_user.isEmpty()) {
        return;
      }
      if (new_user.length() > 25) {
        gui.showError("Username too long");
        return;
      }
      if (state.getMessages(new_user) == null) {
        initSenderView(gui, state, new_user);
      }
    });

    gui.mainPage.fileTransferButton.addActionListener((e) -> {
      selectedFile = null;
      JFrame frame = gui.makeFrame("File transfer",400,200);

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
          Message file_req = new Message(state.getUsername(), state.getCurrentConversation(), "file", new Date(), Type.SIGNAL);
          client.sendMessage(file_req);

          JButton openFile = new JButton(selectedFile.getName());
          File file = selectedFile;
          openFile.addActionListener((Test) -> {
            try {
              FileViewer fileViewer = FileViewerFactory.createFileViewer(file);
              fileViewer.open();
            } catch (UnsupportedFileType e1) {
              gui.showError("Unsupported file type");
            } catch (IOException e1) {
              gui.showError("Failed to open file");
            }
          });
          gui.mainPage.chat.add(openFile);
        }
      });
    });

    gui.mainPage.videoStreamButton.addActionListener((e) -> {
      selectedStreamFile = null;
      JFrame frame = gui.makeFrame("Video stream",400,200);

      JButton streamFile = new JButton("Stream file");
      streamFile.setFont(new Font("Arial", Font.BOLD, 15));
      JButton selectFile = new JButton("Select file");
      selectFile.setFont(new Font("Arial", Font.BOLD, 15));

      JPanel buttons = new JPanel(new GridLayout(1,2));
      buttons.add(selectFile);
      buttons.add(streamFile);

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


      selectFile.addActionListener((select) -> {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          selectedStreamFile = fileChooser.getSelectedFile();
          currentFile.setText("Current file: " + selectedStreamFile.getName());
        }
      });

      streamFile.addActionListener((send) -> {
        if (selectedStreamFile != null){
          JOptionPane.showMessageDialog(null, "Streaming: " + selectedStreamFile.getName() , "Video stream", JOptionPane.INFORMATION_MESSAGE);
          frame.dispose();
          gui.mainPage.addChat(gui.startPage.username.getText() + " is streaming: " + selectedStreamFile.getName());
          Message stream_req = new Message(state.getUsername(), state.getCurrentConversation(), "stream", new Date(), Type.SIGNAL);
          client.sendMessage(stream_req);
        }
      });
    });

    gui.mainPage.videoCallButton.addActionListener((e) -> {
      Message file_req = new Message(state.getUsername(), state.getCurrentConversation(), "webcam", new Date(), Type.SIGNAL);
      client.sendMessage(file_req);

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.setBackground(gui.backColor);

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

  public static JButton initSenderView(Gui gui, ClientState state, String sender_name){
    JButton chat = gui.mainPage.createNewUserButton(sender_name);
    state.initialiseConversation(sender_name);

    chat.addActionListener((action) -> {
      state.setCurrentConversation(sender_name);
      gui.mainPage.updateButtons(chat);
      gui.mainPage.switchChat(state.getMessages(sender_name));
    });
    gui.mainPage.users.revalidate();
    buttonMap.put(sender_name, chat);
    return chat;
  }
}
