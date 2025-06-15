package Message;

import java.util.UUID;

public class GameCreateMessage extends Message {

  private final GameType gameType;

  public GameCreateMessage(String sender, String recipient, GameType gameType, Type type) {
    super(sender, recipient, type);
    this.gameType = gameType;
  }

  public GameCreateMessage(UUID identifier, String sender, String recipient, GameType gameType, Type type) {
    super(identifier, sender, recipient, type);
    this.gameType = gameType;
  }

  public GameType getGameType() {
    return gameType;
  }

}
