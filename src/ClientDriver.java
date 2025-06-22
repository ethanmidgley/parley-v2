import Client.*;
import Client.Components.FilePicker.FilePicker;
import Client.Components.GamePicker.GamePicker;
import Client.Components.Games.GameWindow;
import Client.Components.Games.GameWindowFactory;
import Client.ViewableMessage.ViewableFileMessage;
import Client.ViewableMessage.ViewableTextMessage;
import Games.Blackjack.BlackjackState;
import Message.*;
import Message.Game.GameCreateMessage;
import Message.Game.GameJoinMessage;
import Message.Game.GameType;
import VideoStreamer.FileReceiver;
import VideoStreamer.FileStreamer;
import VideoStreamer.WebcamStreamerReceiver;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

public class ClientDriver {

  public static Client client;
  public static ClientState state;
  static HashMap<String, JButton> buttonMap = new HashMap<String, JButton>();
  static FileStreamer fileStreamer = null;
  static FileReceiver fileReceiver = null;
  static WebcamStreamerReceiver webcamStreamerReceiver = null;

  private static void shutdownStreamerReceiver() {
    if (webcamStreamerReceiver != null) {
      System.out.println("shutting down webcam streamer/receiver");
      webcamStreamerReceiver.shutdown();
      webcamStreamerReceiver = null;
    }
    if (fileReceiver != null) {
      System.out.println("shutting down receiver from client to reconstruct another: ClientDriver, line 76");
      fileReceiver.shutdown();
      fileReceiver = null;
    }
    if (fileStreamer != null) {
      System.out.println("shutting down streamer from client to reconstruct another: ClientDriver, line 71");
      fileStreamer.shutdown();
      fileStreamer = null;
    }
  }

  public static void main(String[] args) {


    Gui gui = new Gui();

    state = new ClientState();
    client = new Client();

    ClientDriver.initSenderView(gui, state, "Chatroom"); // creates chatroom button


    Map<Type, MessageReceivedEvent> callbacks = new HashMap<Type, MessageReceivedEvent>();

    callbacks.put(Type.TEXT,  (MessageReceivedEvent<TextMessage>) (TextMessage message) ->  {

      //  Check to see if we have already messaged this persons if not create a button on the side to access the conversation
      if (state.getMessages(message.getSender()) == null) {
        ClientDriver.initSenderView(gui, state, message.getSender());
      }
      ViewableTextMessage viewableTextMessage = new ViewableTextMessage(message);
      state.addMessageBySender(viewableTextMessage);
      gui.mainPage.addIfCurrentChat(state.getCurrentConversation(), viewableTextMessage);

    });

    callbacks.put(Type.SERVER, (MessageReceivedEvent<Message>) (Message message) -> {

      // I actually don't think server type is used that much at all so we will just leave this unimplemented
      throw new RuntimeException("server client callback");

    });

    callbacks.put(Type.SUCCESS, (MessageReceivedEvent<SuccessMessage>) (SuccessMessage message) -> {
      // A lot of code that can be returned a success should have a handler set up.
      // But this can be the default where it just shows an information pane
      gui.showSuccess(message.getMessage());
    });

    callbacks.put(Type.ERROR, (MessageReceivedEvent<ErrorMessage>) (ErrorMessage message) -> {
      // A lot of code that can be returned a error should have a handler set up.
      // But this can be the default where it just shows an error pane
      gui.showError(message.getMessage());
    });

    callbacks.put(Type.SIGNAL, (MessageReceivedEvent<SignalMessage>) (SignalMessage message) -> {

      String content = message.getSignalType().name().toLowerCase();

      // this is when the user receives a handshake request, it will ask if they want to allow their peer to receive their ip through the server
      int prompt_input = JOptionPane.showConfirmDialog(gui.mainPage, message.getSender() + " would like to send you a " + content, "Receive " + content + "?", JOptionPane.YES_NO_OPTION);

      if (state.getMessages(message.getSender()) == null) {
        JButton button = ClientDriver.initSenderView(gui, state, message.getSender());
        gui.mainPage.updateButtons(button);
      }

      if (!(state.getCurrentConversation().equals(message.getSender()))) {
        gui.mainPage.switchChat(state.getMessages(message.getSender()));
        state.setCurrentConversation(message.getSender());
        gui.mainPage.updateButtons(buttonMap.get(message.getSender()));
      }

      if (prompt_input == JOptionPane.NO_OPTION) {

        client.sendMessage(message.reply(false));
        return;

      }

      if (prompt_input == JOptionPane.YES_OPTION) {

        shutdownStreamerReceiver();
        switch (message.getSignalType()) {
          case Webcam:

            try {
            webcamStreamerReceiver = new WebcamStreamerReceiver(null);
            webcamStreamerReceiver.start();
            } catch (IOException e) {
              e.printStackTrace();
            } catch (LineUnavailableException e) {
              throw new RuntimeException(e);
            }

            break;
          case Stream:


            try {
              shutdownStreamerReceiver();
              fileReceiver = new FileReceiver();
              fileReceiver.start();
            } catch (LineUnavailableException e) {
              e.printStackTrace();
            } catch (IOException e) {
              e.printStackTrace();
            }


            break;
          case File:

            client.fileServer.registerIncomingFile(message);
            break;

        }

        client.sendMessage(message.reply(true));


      }

    });

    callbacks.put(Type.ONLINE_USERS, (MessageReceivedEvent<OnlineCountMessage>) (OnlineCountMessage message) -> {
      gui.mainPage.onlineUsers.setText("Users online: " + message.getCount());
    });

    callbacks.put(Type.CHATROOM, (MessageReceivedEvent<TextMessage>) (TextMessage message) -> {

      ViewableTextMessage viewableTextMessage = new ViewableTextMessage(message);
      state.addMessagesToChatroom(viewableTextMessage);
      gui.mainPage.addIfCurrentChat(state.getCurrentConversation(), viewableTextMessage);

    });

    callbacks.put(Type.GAME_INVITE, (MessageReceivedEvent<TextMessage>) (TextMessage message) -> {

      int prompt_input = JOptionPane.showConfirmDialog(gui.mainPage, message.getSender() + " invited you to a game", "Join game?", JOptionPane.YES_NO_OPTION);

      if (prompt_input == JOptionPane.NO_OPTION) {

        client.sendMessage(message.errorReply(message.getRecipient() + " declined your invite"));
        return;

      }

      if (prompt_input == JOptionPane.YES_OPTION) {
        GameJoinMessage gameJoinMessage = new GameJoinMessage(state.getUsername(), message.getContent());
        joinGame(gameJoinMessage);
      }

    });

    client.bindMessageReceivedEvent(new MessageReceivedEvent() {

      @Override
      public void trigger(Message message) {

        MessageReceivedEvent cb = callbacks.get(message.getType());
        if (cb == null) {
          System.out.println("Unsupported message type, ignoring");
          return;
        }

        try {
          cb.trigger(message);
        } catch (ClassCastException err) {
          System.out.println("Message is not what it says it is");
        }

      }
    });

    client.bindFileReceivedEvent((ViewableFileMessage fileMessage) -> {

      state.addMessageBySender(fileMessage);
      gui.mainPage.addIfCurrentChat(state.getCurrentConversation(), fileMessage);
      gui.mainPage.chat.revalidate();

    });

    gui.mainPage.sendButton.addActionListener((e) -> {
      String text = gui.mainPage.chatInput.getText();
      if (!text.equals("")) {
        if (state.getCurrentConversation().isEmpty()) {
          Gui.showError("No conversation selected");
          gui.mainPage.chatInput.setText("");
          return;
        }

        TextMessage message = new TextMessage(state.getUsername(), state.getCurrentConversation(), text, Type.TEXT);
        if (message.getRecipient().equals("Chatroom")) {
          message.setType(Type.CHATROOM);
        }

        client.sendMessage(message);

        ViewableTextMessage viewableTextMessage =new ViewableTextMessage(message);
        state.addMessageByRecipient(viewableTextMessage);
        gui.mainPage.addChat(viewableTextMessage);

        gui.mainPage.chatInput.setText("");
      }
    });

    gui.mainPage.logoutButton.addActionListener((e) -> {
      System.exit(0);
    });

    gui.mainPage.changeUserButton.addActionListener((e) -> {

      String currentUsername = state.getUsername();
      String newUsername = JOptionPane.showInputDialog(gui, "Enter your new Username:"); //gets the updated username when the button is clicked through a text box

      if (newUsername == null) {
        return;
      }

      newUsername = newUsername.trim();
      if (newUsername.length() > 25) {
        gui.showError("Username too long");
        return;
      }
      if (newUsername == null || newUsername.isEmpty()) {
        gui.showError("Please enter a username");
        return;
      }

      UsernameMessage mes = new UsernameMessage(currentUsername, newUsername, true);
      String finalNewUsername = newUsername;

      client.sendMessage(mes, (Message response, MessageReceivedEvent next) -> {

        if (response.getType() == Type.SUCCESS) {

          state.setUsername(finalNewUsername);
          Gui.showSuccess("Changed username");

        } else if (response.getType() == Type.ERROR) {
          gui.showError("Failed to update username");
        }
      });
    });

    gui.startPage.loginButton.addActionListener((action) -> {

      String username = gui.startPage.username.getText();
      username = username.trim();

      if (username.isEmpty()) {
        Gui.showError("Please enter a username");
        gui.startPage.username.setText("");
        return;
      }

      if (username.length() > 25) {
        Gui.showError("Username too long");
        gui.startPage.username.setText("");
        return;
      }

      if (gui.startPage.ipAddress.getText().isEmpty() || !isValidIPv4(gui.startPage.ipAddress.getText())) {
        Gui.showError("Please enter a valid IP address");
        gui.startPage.ipAddress.setText("");
        return;
      }

      try {
        client.connectToServer(gui.startPage.ipAddress.getText());

      } catch (IOException e) {
        Gui.showError("Failed to connect to server");
        return;
      }

      List<String> blocked_names = new ArrayList<>();
      blocked_names.add("Chatroom");
      blocked_names.add("Server");
      blocked_names.add("Parley");
      blocked_names.add("ParleyChatroom");

      if (blocked_names.contains(username)) {
        gui.showError("Username not allowed");
        return;
      }

      UsernameMessage prop = new UsernameMessage(username, username, false);
      String finalUsername = username;
      client.sendMessage(prop, (Message response, MessageReceivedEvent next) -> {

        if (response.getType() == Type.SUCCESS) {
          state.setUsername(finalUsername);
          gui.switchPanel("MainPage");
        } else {
          gui.showError("Username already taken");
          try {
            client.disconnectFromServer();
          } catch (IOException e) {
            Gui.showError("Failed to disconnect to server");
            System.exit(0);
          }

        }

      });

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

      new FilePicker("File transfer", (File file) -> {

        if (file != null) {
          JOptionPane.showMessageDialog(null, "Sending: " + file.getName(), "File transfer", JOptionPane.INFORMATION_MESSAGE);
          final String transfer_to = state.getCurrentConversation();

          SignalMessage file_req = new SignalMessage(state.getUsername(), transfer_to,  SignalType.File);

          client.sendMessage(file_req, (Message response, MessageReceivedEvent next) -> {


            if (response.getType() == Type.SIGNAL_ACK) {

              SignalMessage signalResponse = (SignalMessage) response;

              if (signalResponse.isAccepted()) {

                // TODO: ADD A FILE MESSAGE IN TO THE CONVERSATION HERE
                InetAddress peer_address = null;
                try {
                  peer_address = InetAddress.getByName(signalResponse.getAddress());

                } catch (UnknownHostException _e) {
                  System.out.println("Could not find with peer");
                  return;

                }

                client.sendFile(peer_address, file_req.getId(), file);
                ViewableFileMessage viewableFileMessage = new ViewableFileMessage(file_req, file);
                gui.mainPage.addIfCurrentChatRecipient(state.getCurrentConversation(), viewableFileMessage);
                state.addMessageByRecipient(viewableFileMessage);

              } else {
                gui.showError(transfer_to + " denied your file");
              }

            } else {
              next.trigger(response);
            }

          });
        }
      });
    });

    gui.mainPage.videoStreamButton.addActionListener((e) -> {

        new FilePicker("File Stream", (File file) -> {
        if (file != null) {
          JOptionPane.showMessageDialog(null, "Streaming: " + file.getName(), "Video stream", JOptionPane.INFORMATION_MESSAGE);
          Message stream_req = new SignalMessage(state.getUsername(), state.getCurrentConversation(),  SignalType.Stream);
          client.sendMessage(stream_req, (Message response, MessageReceivedEvent next) -> {

            if (response.getType() == Type.SIGNAL_ACK) {

              SignalMessage signalResponse = (SignalMessage) response;

              if (signalResponse.isAccepted()) {

                InetAddress peer_address = null;
                try {
                  peer_address = InetAddress.getByName(signalResponse.getAddress());

                } catch (UnknownHostException _e) {
                  System.out.println("Could not find with peer");
                  return;

                }

                try {
                  shutdownStreamerReceiver();
                  fileStreamer = new FileStreamer(peer_address, file);
                  fileStreamer.start();
                } catch (IOException e2) {
                  e2.printStackTrace();
                } catch (LineUnavailableException e2) {
                  e2.printStackTrace();
                }

              } else {

                gui.showError("Denied your file stream");

              }

            } else {
              next.trigger(response);
            }

          });
        }
      });
    });

    gui.mainPage.videoCallButton.addActionListener((event) -> {
      SignalMessage file_req = new SignalMessage(state.getUsername(), state.getCurrentConversation(), SignalType.Webcam);
      client.sendMessage(file_req, (Message response, MessageReceivedEvent next) -> {


        if (response.getType() == Type.SIGNAL_ACK) {
          SignalMessage signalResponse = (SignalMessage) response;

          if (signalResponse.isAccepted()) {

            InetAddress peer_address = null;
            try {
              peer_address = InetAddress.getByName(signalResponse.getAddress());

            } catch (UnknownHostException _e) {
              System.out.println("Could not find with peer");
              return;

            }

            try {
              shutdownStreamerReceiver();
              webcamStreamerReceiver = new WebcamStreamerReceiver(peer_address);
              webcamStreamerReceiver.start();
            } catch (IOException e) {
              e.printStackTrace();
            } catch (LineUnavailableException e) {
              throw new RuntimeException(e);
            }


          } else {

            gui.showError("Denied your video call");

          }

        } else {
          next.trigger(response);
        }


      });
    });

    gui.mainPage.createGameButton.addActionListener((e) -> {

      GamePicker.Pick((GameType g ) -> {
        GameCreateMessage msg = new GameCreateMessage(state.getUsername(), g);
        joinGame(msg);
      });


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

      if (sender_name.equals("Chatroom")) {
        gui.mainPage.buttonsPanel.setVisible(false);
      }

    });
    gui.mainPage.users.revalidate();
    buttonMap.put(sender_name, chat);
    return chat;
  }

  public static void joinGame(Message msg) {

    client.sendMessage(msg, (Message response, MessageReceivedEvent next) -> {
      if (response.getType() == Type.GAME_JOIN_SUCCESS) {
        GameCreateMessage<BlackjackState> createMessage = (GameCreateMessage) response;

        // So now we have to create the screen and handler interceptors
        GameWindowFactory.createGameWindow(createMessage.getGameType(), createMessage.getSender(), client, state, createMessage.getGameState());
      } else {
        next.trigger(response);
      }

    });

  }
}
