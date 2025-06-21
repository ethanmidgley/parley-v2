package Games.Blackjack;

import ConnectedClient.ConnectedClient;
import Games.Blackjack.utils.Card;
import Games.DeepCopyable;
import Games.GameState;
import Games.Turn;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlackjackState extends GameState<BlackjackState>  {

  public int round;
  public boolean roundOver;
  public Map<String, PlayerState> playerState;
  public List<Card> dealerCards;
  public Turn<String, Action> turn;

  public BlackjackState(boolean gameStarted, int round, boolean roundOver, Map<String, PlayerState> playerState, List<Card> dealerCards, Turn<String, Action> turn) {
    super(gameStarted);
    this.round = round;
    this.roundOver = roundOver;
    this.playerState = playerState;
    this.turn = turn;
    this.dealerCards = dealerCards;

  }

  @Override
  public BlackjackState deepCopy() {

//     this should probably be moved to the other file
    Map<String, PlayerState> playerState = this.playerState.entrySet().stream()
            .map((entry) -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().deepCopy()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    Turn<String, Action> turn = null;
    if (this.turn != null) {
      List<Action> validTurns = new ArrayList<>();
      validTurns.addAll(this.turn.validActions);
      turn = new Turn<>(this.turn.player, validTurns);
    }

    List<Card> dealerCopyCards = this.dealerCards.stream().map(Card::deepCopy).toList();

    return new BlackjackState(gameStarted, round, roundOver, playerState, dealerCopyCards, turn);

  }
}
