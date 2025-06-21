package Client.Components.Games;

import Client.Client;
import Client.ClientState;
import Client.Components.Button.ButtonFactory;
import Client.Gui;
import Games.Blackjack.BlackjackState;
import Games.GameState;
import Message.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import Client.MessageReceivedEvent;
import Message.Game.GameMoveMessage;
import Message.Game.GameStateMessage;

import java.util.HashMap;
import java.util.Map;

public abstract class GameWindow<TGameState extends GameState, TGameMove>{

  private Client client;
  private String game_id;
  public JFrame frame;
  public JPanel body;
  public JPanel content;
  private Map<Type, MessageReceivedEvent> callbacks;
  private ClientState clientState;


  private TGameState previousState;

  public GameWindow(String game_id, String game_name, Client client, ClientState clientState, TGameState initialState) {

    this.clientState = clientState;
    this.client = client;
    this.callbacks = new HashMap<>();
    this.game_id = game_id;
    frame = Gui.makeFrame(game_name, 1200, 1000);
    this.previousState = initialState;


    JPanel header = new JPanel();
    header.setLayout(new BorderLayout());
    header.setOpaque(false);
    header.setBorder(new EmptyBorder(5, 5, 5, 5));
    header.setPreferredSize(new Dimension(1200, 100));


    JButton inviteButton = ButtonFactory.createSkinny("Invite");
    inviteButton.addActionListener(e -> {

      String invite_name = JOptionPane.showInputDialog("Please enter your name of person to invite");

      if (invite_name == null) {
        return;
      }

      if (clientState.getUsername().equals(invite_name)) {
        Gui.showError("You cannot invite yourself");
        return;
      }

      client.sendMessage(new TextMessage(null, invite_name, game_id, Type.GAME_INVITE));

    });

    JButton startButton = ButtonFactory.createSkinny("Start");
    startButton.addActionListener(e -> {
      client.sendMessage(new TextMessage(null, game_id, null, Type.GAME_START));
    });
    startButton.setVisible(true);

    header.add(startButton, BorderLayout.WEST);
    header.add(inviteButton, BorderLayout.EAST);

    content = new JPanel();
    content.setLayout(new BorderLayout());
    content.setOpaque(false);
    content.setPreferredSize(new Dimension(1100, 900));

    body = new JPanel();
    body.setLayout(new BorderLayout());
    body.setPreferredSize(new Dimension(1200, 1000));

    body.setLayout(new BorderLayout());
    body.add(header, BorderLayout.NORTH);
    body.add(content, BorderLayout.CENTER);

    frame.add(body);

    client.registerGameLobby(game_id, this);

    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        client.sendMessage(new Message(null, game_id, Type.GAME_LEAVE));
        client.unregisterGameLobby(game_id);
      }
    });

    callbacks.put(Type.GAME_STATE, (MessageReceivedEvent<GameStateMessage<TGameState>>) (GameStateMessage<TGameState> msg) -> {

      if (msg.getGameState().gameStarted) {
        startButton.setVisible(false);
      }
      render(msg.getGameState());
      previousState = msg.getGameState();
    });

    callbacks.put(Type.GAME_NOTIFICATION, (MessageReceivedEvent<TextMessage>) (TextMessage msg) -> {
      Gui.showInfo(msg.getContent());
    });

    callbacks.put(Type.ERROR, (MessageReceivedEvent<ErrorMessage>) (ErrorMessage msg) -> {
      Gui.showError(msg.getMessage());
    });

  }

  public abstract void render(TGameState state);

  public void trigger(Message message) {
    MessageReceivedEvent event = callbacks.get(message.getType());
    if (event != null) {
      event.trigger(message);
    }
  }

  public TGameState getPreviousState() {
    return previousState;
  }

  public ClientState getClientState() {
    return clientState;
  }

  public void sendMove(TGameMove move) {
    GameMoveMessage<TGameMove> moveMessage = new GameMoveMessage<>(null, game_id, move);
    client.sendMessage(moveMessage);
  }

}
