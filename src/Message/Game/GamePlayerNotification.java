package Message.Game;

import Message.Message;
import Message.Type;

import java.util.UUID;

public class GamePlayerNotification<TGameState> extends Message {

  private final TGameState gameState;
  private final String content;

  public GamePlayerNotification(String sender, String recipient, String content, TGameState gameState) {
    super(sender, recipient, Type.GAME_NOTIFICATION);
    this.gameState = gameState;
    this.content = content;
  }

  public GamePlayerNotification(UUID identifier, String sender, String recipient, String content, TGameState gameState) {
    super(identifier, sender, recipient, Type.GAME_NOTIFICATION);
    this.gameState = gameState;
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public TGameState getGameState() {
    return gameState;
  }

}
