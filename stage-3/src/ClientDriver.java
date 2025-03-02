import java.io.IOException;
import java.util.Date;
import javax.swing.JOptionPane;

public class ClientDriver {

  public static void main(String[] args) {

    ClientState state = new ClientState();

    Gui gui = new Gui();

    Client client = new Client((Message message) -> {
        if (message.getType() == Type.TEXT) {
          state.addMessageBySender(message);
//          System.out.printf("\033[2K\r%s: %s\n", message.getSender(), message.getContent());
        } else {
          System.out.println("\033[2K\rError - Received incorrect message type");
        }
    });

    gui.mainPage.sendButton.addActionListener((e) -> {
      String text = gui.mainPage.chatInput.getText();
      if (!text.equals("")){

        Message message = new Message(state.getUsername(), state.getCurrentConversation(), text, new Date(), Type.TEXT);
        client.sendMessage(message);
        state.addMessageByRecipient(message);

        // shitty way of updating the chat
        // fucking up the chat
        // been fucking up the chat
        // get me a beer
        // open fridge
        // fride.getBeer()
        // anything else
        // me: what is your favourite pokemon
        // fridge: I am a fridge

        gui.mainPage.addChat("You: " + text);
        gui.mainPage.chatInput.setText("");
      }
    });


    gui.mainPage.logoutButton.addActionListener((e) -> {
      System.out.println("Logging out");
      gui.startPage.clearFields();
      gui.switchPanel("StartPage");
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
      
      System.out.println(new_user);
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
}
