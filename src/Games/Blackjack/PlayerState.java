package Games.Blackjack;

import Games.Blackjack.utils.Card;
import Games.Blackjack.utils.Hand;
import Games.IllegalMoveException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlayerState implements Serializable {
  // current cards
  public List<Card> cards;
  // in round
  public boolean in_round;
  // bet amount
  public int bet_amount;
  // balance
  public int balance;
  public boolean sticking;
  public Outcome outcome;

  public PlayerState() {
    this.cards = new ArrayList<>();
    this.in_round = false;
    this.bet_amount = 0;
    this.sticking = false;
    this.outcome = Outcome.PENDING;
    this.balance = 1000;
  }


  public PlayerState(List<Card> cards, boolean in_round, int bet_amount, int balance, boolean sticking, Outcome outcome) {
    this.cards = cards;
    this.in_round = in_round;
    this.bet_amount = bet_amount;
    this.balance = balance;
    this.sticking = sticking;
    this.outcome = outcome;
  }

  public PlayerState deepCopy() {
    return new PlayerState(cards.stream().map(Card::deepCopy).toList(), in_round, bet_amount, balance, sticking, outcome);
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("PlayerState {")
            .append("\n  Cards: ").append(cards) // toString for cards list
            .append("\n  In Round: ").append(in_round)
            .append("\n  Bet Amount: ").append(bet_amount)
            .append("\n  Balance: ").append(balance)
            .append("\n  Sticking: ").append(sticking)
            .append("\n  Outcome: ").append(outcome)
            .append("\n}");

    return sb.toString();
  }


  public void newRound() {
    cards.clear();
    in_round = true;
    bet_amount = 0;
    sticking = false;
    outcome = Outcome.PENDING;
  }


  public boolean isBust() {
    return Hand.calculateValue(cards) > 21;
  }

  public int handValue() {
    return Hand.calculateValue(cards);
  }

  public void betAmount(int amount) {
    if (balance < amount) {
      throw new IllegalMoveException("Cannot bet more than you have");
    }
    bet_amount += amount;
    balance -= amount;
  }

  public void addCard(Card card) {
    cards.add(card);
  }

  public void stick() {
    this.sticking = true;
  }

  public boolean hasBet() {
    return bet_amount > 0;
  }

  public void setWin() {
    this.outcome = Outcome.WIN;
  }

  public void setLose() {
    this.outcome = Outcome.LOSE;
  }

  public List<Action> nextValidActions() {

    List<Action> actions = new ArrayList<>();
    // If they are in a round then they actually have actions
    if (!in_round) {
      return actions;
    }

    if (!hasBet()) {
      actions.add(Action.BET);
      return actions;
    }

    if (!isBust() && !sticking) {
      actions.add(Action.STICK);
      actions.add(Action.HIT);
    }
    return actions;
  }

}
