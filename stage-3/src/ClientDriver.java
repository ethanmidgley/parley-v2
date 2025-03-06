import java.io.IOException;
import java.util.Date;
import javax.swing.*;

public class ClientDriver {

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

          case SIGNAL -> {}

          case SERVER -> {
            System.out.println("Server message receieved");
          }

          case CHATROOM -> {
            state.addMessageBySender(message);

            if (state.getCurrentConversation().equals("Chatroom")){
              gui.mainPage.addChat(message.getSender() + ": " + message.getContent());
            }
          }

          default -> {
            System.out.println("\033[2K\rError - Received incorrect message type");
          }
        }
    });

    gui.mainPage.sendButton.addActionListener((e) -> {
      String text = gui.mainPage.chatInput.getText();
      if (!text.equals("")){

        Message message = new Message(state.getUsername(), state.getCurrentConversation(), text, new Date(), Type.TEXT);
        client.sendMessage(message);
        state.addMessageByRecipient(message);

        gui.mainPage.addChat(state.getUsername() + ": " + text);
        gui.mainPage.chatInput.setText("");
      }
    });


    gui.mainPage.logoutButton.addActionListener((e) -> {
      gui.startPage.clearFields();
      gui.switchPanel("StartPage");
      System.exit(0);
  });

    gui.startPage.loginButton.addActionListener((action) -> {
      if (gui.startPage.username.getText().isEmpty()) {
        JOptionPane.showMessageDialog(gui.startPage, "Please enter a username", "Error", JOptionPane.ERROR_MESSAGE);
      }
      if (gui.startPage.ipAddress.getText().isEmpty() || !isValidIPv4(gui.startPage.ipAddress.getText())) {
        JOptionPane.showMessageDialog(gui.startPage, "Please enter a valid IP address", "Error", JOptionPane.ERROR_MESSAGE);
        gui.startPage.ipAddress.setText("");
      }
      
      else{
        System.out.println("Logging in as " + gui.startPage.username.getText() + " to server " + gui.startPage.ipAddress.getText());

        try {
          client.connectToServer(gui.startPage.ipAddress.getText());
          Message prop = new Message(gui.startPage.username.getText(), "Server",gui.startPage.username.getText(), new Date(), Type.USERNAME_PROPAGATE);
          client.sendMessage(prop);
          state.setUsername(gui.startPage.username.getText());
          gui.switchPanel("MainPage");
        } catch (IOException e) {
          JOptionPane.showMessageDialog(gui.startPage, "Failed to connect to server", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    gui.mainPage.newChatButton.addActionListener((e) -> {
      String new_user = JOptionPane.showInputDialog(gui.mainPage, "Who do you want to message?", "New Chat", JOptionPane.QUESTION_MESSAGE);

      // Add them to the user list? and when they do an onclick change the state to the username
      if (state.getMessages(new_user) != null) {
        JOptionPane.showMessageDialog(gui.mainPage, "You already have a conversation with this person", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      JButton chat = gui.mainPage.createNewUserButton(new_user);
      state.initialiseConversation(new_user);
      chat.addActionListener((action) -> {
        state.setCurrentConversation(new_user);
        gui.mainPage.switchChat(state.getMessages(new_user));
        System.out.println("New user: " + new_user);
      });
      gui.mainPage.users.revalidate();

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
      gui.mainPage.switchChat(state.getMessages(sender_name));
    });
    gui.mainPage.users.revalidate();
  }

}
