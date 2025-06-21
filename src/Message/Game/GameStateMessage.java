package Message.Game;

import Message.Message;
import Message.Type;

import java.util.Date;

public class GameStateMessage<GameState> extends Message {

  private final GameState gameState;

  public GameStateMessage(String sender, String recipient, GameState state) {
    super(sender, recipient, Type.GAME_STATE);
    this.gameState = state;
  }

  public GameState getGameState() {
    return gameState;
  }
}
