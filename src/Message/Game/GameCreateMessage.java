package Message.Game;

import Message.*;

import java.util.UUID;

public class GameCreateMessage<TGameState> extends Message {

  private final GameType gameType;
  private final TGameState gameState;

  public GameCreateMessage(String sender, GameType gameType) {
    super(sender, null, Type.GAME_INSTANTIATE);
    this.gameType = gameType;
    this.gameState = null;
  }

  public GameCreateMessage(String sender, String recipient, GameType gameType) {
    super(sender, recipient, Type.GAME_INSTANTIATE);
    this.gameType = gameType;
    this.gameState = null;
  }

  public GameCreateMessage(UUID identifier, String sender, String recipient, GameType gameType) {
    super(identifier, sender, recipient, Type.GAME_INSTANTIATE);
    this.gameType = gameType;
    this.gameState = null;
  }

  public GameCreateMessage(UUID identifier, String sender, String recipient, GameType gameType, TGameState gameState) {
    super(identifier, sender, recipient, Type.GAME_JOIN_SUCCESS);
    this.gameType = gameType;
    this.gameState = gameState;
  }

  public TGameState getGameState() {
    return gameState;
  }

  public GameType getGameType() {
    return gameType;
  }
}
