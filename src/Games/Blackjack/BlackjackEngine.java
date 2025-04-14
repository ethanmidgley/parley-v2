package Games.Blackjack;

import Games.GameEngine;
import Games.IllegalMoveException;

public class BlackjackEngine extends GameEngine<BlackjackUpdate,BlackjackMove> {

  public BlackjackEngine() {
    super();
  }

  public BlackjackEngine(int TABLE_CAPACITY) {
    super(TABLE_CAPACITY);
  }

  @Override
  public void handleMove(BlackjackMove blackjackMove) throws IllegalMoveException {

    // calculated next update lets push it
    pushUpdate(new BlackjackUpdate());

  }


}
