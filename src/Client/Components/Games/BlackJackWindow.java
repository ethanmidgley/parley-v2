package Client.Components.Games;

import Client.Client;
import Client.Components.Button.ButtonFactory;
import Client.Components.HandComponent;
import Games.Blackjack.*;
import Client.Gui;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import Client.ClientState;
import Games.Blackjack.Action;
import Games.Blackjack.utils.Card;

import javax.swing.*;


public class BlackJackWindow extends GameWindow<BlackjackState, BlackjackMove> {

  private JPanel actionButtons;
  private JButton hitButton;
  private JButton betButton;
  private JButton stickButton;
  private JPanel table;
  private List<JPanel> playerPanels;

  private int[][] player_placements = {{0,3},{4,3},{0,2},{4,2}};

  public BlackJackWindow(String game_id, String game_name, Client client, ClientState state, BlackjackState blackjackState) {
    super(game_id, game_name, client, state, blackjackState);

    body.setBackground(new Color(54, 128, 11));

    playerPanels = new ArrayList<>();

    table = new JPanel();
    table.setOpaque(false);
    table.setLayout(new GridBagLayout());
    table.setPreferredSize(new Dimension(1200,800));

    actionButtons = new JPanel();
    actionButtons.setOpaque(false);
    actionButtons.setLayout(new FlowLayout(FlowLayout.CENTER));
    actionButtons.setVisible(false);

    hitButton = ButtonFactory.create("Hit", new Dimension(150, 50));
    hitButton.addActionListener(e -> {
      sendMove(new BlackjackMove(Action.HIT));
    });

    stickButton = ButtonFactory.create("Stick", new Dimension(150, 50));
    stickButton.addActionListener(e -> {
      sendMove(new BlackjackMove(Action.STICK));
    });


    betButton = ButtonFactory.create("Bet", new Dimension(150, 50));
    betButton.addActionListener(e -> {

      PlayerState playerState = getPreviousState().playerState.get(getClientState().getUsername());

      String betAmount = JOptionPane.showInputDialog("Please enter bet amount:\nBalance is £" + playerState.balance);

      if (betAmount == null) {
        return;
      }


      int amount;
      try {
        amount = Integer.parseInt(betAmount);
      } catch (NumberFormatException ex) {
        Gui.showError("Input not a number, try again");
        return;
      }

      if (amount <= 0) {
        Gui.showError("Cannot bet nothing, try again");
        return;
      }

      if (amount > playerState.balance) {
        Gui.showError("Cannot bet more then you possess, try again");
        return;
      }

      sendMove(new BlackjackMove(Action.BET, amount));

    });

    actionButtons.add(hitButton);
    actionButtons.add(stickButton);
    actionButtons.add(betButton);

    content.add(table, BorderLayout.CENTER);
    content.add(actionButtons, BorderLayout.SOUTH);

    render(blackjackState);
    frame.setVisible(true);

  }

  public JPanel textColumn(String lines[]) {

    JPanel panel = new JPanel();
    panel.setOpaque(false);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    Font f = new Font("Arial", Font.PLAIN, 18);
    for (String line : lines) {
      JLabel text = new JLabel(line, SwingConstants.CENTER);
      text.setFont(f);
      text.setAlignmentX(Component.CENTER_ALIGNMENT);
      panel.add(text, BorderLayout.CENTER);
    }

    return panel;
  }

  public JPanel playerPanel(String name, PlayerState state) {

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setVisible(true);

    JPanel texts = new JPanel();
    texts.setOpaque(false);
    texts.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));

    texts.add(textColumn(new String[]{"Player", name}));
    texts.add(textColumn(new String[]{"Balance", "£"+(state.balance)}));
    texts.add(textColumn(new String[]{"Bet amount", "£"+(state.bet_amount)}));

    JPanel cards = new HandComponent(state.cards);
    panel.add(cards, BorderLayout.CENTER);
    panel.add(texts, BorderLayout.SOUTH);
    panel.setOpaque(false);

    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    return panel;

  }

  public void render(BlackjackState state) {

    if (state.turn != null) {
      if (state.turn.player.equals(getClientState().getUsername())) {
        actionButtons.setVisible(true);

        // hide all action buttons and then only show ones that are valid
        hitButton.setVisible(false);
        stickButton.setVisible(false);
        betButton.setVisible(false);

        if (state.turn.validActions.contains(Action.BET)) {
          betButton.setVisible(true);
        }

        if (state.turn.validActions.contains(Action.STICK)) {
          stickButton.setVisible(true);
        }

        if (state.turn.validActions.contains(Action.HIT)) {
          hitButton.setVisible(true);
        }


      } else {
        actionButtons.setVisible(false);
      }
    } else {
      actionButtons.setVisible(false);
    }

    table.removeAll();
    table.revalidate();

    GridBagConstraints dealer_constraints = new GridBagConstraints();
    dealer_constraints.gridx = 2;
    dealer_constraints.gridy = 0;
    JPanel dealerHand = new HandComponent(state.dealerCards);
    dealerHand.setPreferredSize(new Dimension(200, 200));
    table.add(dealerHand, dealer_constraints);

    if (state.playerState != null) {

      GridBagConstraints player_constraints = new GridBagConstraints();
      player_constraints.gridx = 2;
      player_constraints.gridy = 5;
      PlayerState yourState = state.playerState.get(getClientState().getUsername());

      if (yourState.outcome == Outcome.WIN || yourState.outcome == Outcome.LOSE) {
        SwingUtilities.invokeLater(() -> {
          Gui.showInfo("You " + yourState.outcome.name().toLowerCase());
        });

      }


      JPanel yourPanel = playerPanel("You", yourState);
      table.add(yourPanel,player_constraints);

      int i = 0;
      for (Map.Entry<String, PlayerState> p: state.playerState.entrySet()) {

        if (p.getKey().equals(getClientState().getUsername())) {continue;}

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = player_placements[i][0];
        constraints.gridy = player_placements[i][1];
        JPanel panel = playerPanel(p.getKey(), p.getValue());
        table.add(panel,constraints);
        i++;

      }

    }
    table.revalidate();

  }

}
