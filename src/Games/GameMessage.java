package Games;

import Message.*;

import java.util.Date;

public class GameMessage<GameMove> extends Message {

  private final GameMove gameMove;

  public GameMessage(String sender, String recipient, GameMove move, Date date) {
    super(sender, recipient, Type.GAME_MOVE);
    this.gameMove = move;
  }

  public GameMove getGameMove() {
    return gameMove;
  }
}
