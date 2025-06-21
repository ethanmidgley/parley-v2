package Message.Game;

import Message.*;

import java.util.Date;

public class GameMoveMessage<GameMove> extends Message {

  private final GameMove gameMove;

  public GameMoveMessage(String sender, String recipient, GameMove move) {
    super(sender, recipient, Type.GAME_MOVE);
    this.gameMove = move;
  }

  public GameMove getGameMove() {
    return gameMove;
  }

}
