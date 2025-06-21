package Games.Blackjack;

import ConnectedClient.*;
import Games.Blackjack.utils.Card;
import Games.Blackjack.utils.Hand;
import Games.GameState;
import Games.Turn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlackjackInternalState extends GameState {

  int round;
  boolean roundOver;
  Map<ConnectedClient, PlayerState> playerState;
  List<Card> dealerCards;
  Turn<ConnectedClient, Action> turn;


  public BlackjackInternalState() {
    super(false);
    round = 0;
    roundOver = false;
    playerState = new HashMap<ConnectedClient, PlayerState>();
    dealerCards = new ArrayList<Card>();
    turn = null;
  }

  public double payoutAmount(int handValue) {
    if (handValue > 21) return 0;
    if (handValue == 21) return 2.5;
    return 2;
  }

  public void newRound() {
    round++;
    dealerCards.clear();
    roundOver = false;
    playerState.values().forEach(PlayerState::newRound);
  }


  public void collectAndPayout() {

    int dealerHandValue = Hand.calculateValue(dealerCards);
    roundOver = true;

    for (Map.Entry<ConnectedClient, PlayerState> e : playerState.entrySet()) {

      int playerHandValue = (e.getValue().handValue());

      if (e.getValue().hasBet()) {

        // conditions to win
        // Dealer goes bust, or if the player has a larger hand and not bust

        // This is so wrong, maybe not anymore
        if ((dealerHandValue > 21 && playerHandValue <= 21 ) || (playerHandValue > dealerHandValue && playerHandValue <= 21)) {
          e.getValue().balance += payoutAmount(playerHandValue) * e.getValue().bet_amount;
          e.getValue().setWin();
        } else {
          e.getValue().setLose();
        }

        e.getValue().bet_amount = 0;

        if (e.getValue().balance == 0) {
          e.getValue().balance = 100;
        }

      }

    }

  }

  @Override
  public GameState deepCopy() {
    return null;
  }
}
