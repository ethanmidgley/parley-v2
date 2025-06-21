package Games.Blackjack;

import ConnectedClient.ConnectedClient;
import Games.Blackjack.utils.Card;
import Games.Blackjack.utils.Deck;
import Games.Blackjack.utils.Hand;
import Games.GameEngine;
import Games.IllegalMoveException;
import Games.Turn;

import java.util.*;
import java.util.stream.Collectors;

public class BlackjackEngine extends GameEngine<BlackjackMove,BlackjackInternalState,BlackjackState> {

  BlackjackInternalState state;
  Deck deck;

  public BlackjackEngine() {
    super();
    state = new BlackjackInternalState();
    deck = new Deck(6);
    deck.shuffle();
  }

  public BlackjackEngine(int TABLE_CAPACITY) {
    super(TABLE_CAPACITY);
    state = new BlackjackInternalState();
    deck = new Deck(6);
    deck.shuffle();
  }

  @Override
  public void handleMove(ConnectedClient player, BlackjackMove move) throws IllegalMoveException {

    if (!state.turn.player.getIdentifier().equals(player.getIdentifier())) {
      throw new IllegalMoveException("not their turn");
    }

    if (!state.turn.validActions.contains(move.action)) {
      throw new IllegalMoveException("unexpected action");
    }

    PlayerState pState = state.playerState.get(player);

    switch (move.action) {

      case BET -> {
        pState.betAmount(move.bet_amount);

        Turn<ConnectedClient, Action> nextBet = nextBetAction();

        // More bets to be collected
        if (nextBet != null) {
          state.turn = nextBet;
          pushState(state);
          return;
        }

        // No more bets to collect, lets deal cards
        dealEveryoneCards();
        state.turn = null;
        pushState(state);

        // Lets now check to see if dealer has black jack
        if (Hand.calculateValue(state.dealerCards) == 21) {
          // Dealer has blackjack
          // lets collect debts

          state.collectAndPayout();
          state.roundOver = true;
          pushState(state);

          // Create a new round now? and then push it
          state.newRound();
          deck = new Deck(6);
          deck.shuffle();
          state.turn = nextTurn();
          pushState(state);

          // send messages
          // and then new round
          return;
        }

        // Round is good to go. Let's get the next persons turn then
        state.turn = nextTurn();
        pushState(state);
        return;


      }
      case HIT -> {
        pState.addCard(deck.deal());
        // If they are not bust we are going to ask them again with the same possible actions
        if (!pState.isBust()) {
          pushState(state);
          return;
        }
      }
      case STICK -> {
        // then we just rotate to the next player
        pState.stick();
      }

    }

    Turn<ConnectedClient, Action> nextTurn = nextTurn();
    if (nextTurn != null) {
      state.turn = nextTurn;
      pushState(state);
      return;
    }

    // There is no next turn, dealers turn then
    // Dealer will hit until their value is 17 or more
    state.turn = null;
    while (Hand.calculateValue(state.dealerCards) < 17) {
      state.dealerCards.add(deck.deal());
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      pushState(state);
    }


    // Calculate wins & losses
    state.collectAndPayout();
    pushState(state);

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    // Wait and then start new round.
    state.newRound();
    deck = new Deck(6);
    deck.shuffle();
    state.turn = nextTurn();

    pushState(state);
  }

  public void startGame() {
    state.newRound();
    state.gameStarted = true;
    state.turn = nextTurn();
    pushState(state);
  }


  // this is where we will set them up in the internal state with not being in the current round
  public void handlePlayerJoin(ConnectedClient player)  {
    state.playerState.put(player, new PlayerState());
  }

  public void handlePlayerLeave(ConnectedClient player) {
    state.playerState.remove(player);
  }

  public BlackjackState convert(BlackjackInternalState stateToConvert) {


    List<Card> dealersHand  = new ArrayList<>();
    if (!stateToConvert.dealerCards.isEmpty()) {

      // We want to redact the dealer cards here.....
      // See if it is anyone elses turn if not it is the dealers turn
      // We need to find if someone is still in the round?
      boolean dealers_turn = true;
      for (PlayerState player : stateToConvert.playerState.values()) {
        if (player.in_round) {
          if (!(player.isBust()|| player.sticking)) {
            dealers_turn = false;
          }
        }
      }

      if (!dealers_turn) {
        dealersHand.add(new Card());
        dealersHand.add(new Card());
      }
      else {
        dealersHand = stateToConvert.dealerCards.stream().map(Card::deepCopy).toList();
      }
    }

//     this should probably be moved to the other file
    Map<String, PlayerState> playerState = stateToConvert.playerState.entrySet().stream()
            .map((entry) -> new AbstractMap.SimpleEntry<>(entry.getKey().getIdentifier(), entry.getValue().deepCopy()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    Turn<String, Action> turn = null;
    if (stateToConvert.turn != null) {
      turn = new Turn<>(stateToConvert.turn.player.getIdentifier(), stateToConvert.turn.validActions);
    }


    return new BlackjackState(stateToConvert.gameStarted, stateToConvert.round, stateToConvert.roundOver, playerState, dealersHand, turn);
  }

  @Override
  public BlackjackState getState() {
    return convert(state);
  }

  public Turn<ConnectedClient, Action> nextBetAction() {

    List<Action> betActions = new ArrayList<>();
    betActions.add(Action.BET);
    for (Map.Entry<ConnectedClient, PlayerState> e : state.playerState.entrySet()) {
      if (!e.getValue().hasBet()) {
        return new Turn<ConnectedClient, Action>(e.getKey(), betActions);
      }

    }
    return null;

  }

  public Turn<ConnectedClient, Action> nextTurn() {

    List<Turn<ConnectedClient, Action>> turns = new ArrayList<>();
    for (Map.Entry<ConnectedClient, PlayerState> e : state.playerState.entrySet()) {

      List<Action> actions = e.getValue().nextValidActions();
      Turn turn = new Turn(e.getKey(), actions);

      if (actions.isEmpty()) continue;

      if (actions.contains(Action.BET)) {
        List<Action> betActions = new ArrayList<>();
        betActions.add(Action.BET);

        Turn t = new Turn(e.getKey(), betActions);
        return t;
      }
      turns.add(turn);

    }

    if (turns.isEmpty()) {
      return null;
    }

    return turns.get(0);

  };

  public void dealEveryoneCards () {
    for (PlayerState pState : state.playerState.values()) {
      if (pState.in_round) {
        dealAmount(pState.cards, 2);
      }
    }
    dealAmount(state.dealerCards, 2);
  }

  public void dealAmount(List<Card> hand, int amount) {
    for (int i = 0; i < amount; i++) {
      hand.add(deck.deal());
    }
  }

}