package Games.Blackjack;

import java.io.Serializable;

public class BlackjackMove implements Serializable {
  Action action;
  int bet_amount;

  public BlackjackMove(Action action) {
    this.action = action;
    this.bet_amount = 0;
  }

  public BlackjackMove(Action action, int bet_amount) {
    this.action = action;
    this.bet_amount = bet_amount;
  }

}
