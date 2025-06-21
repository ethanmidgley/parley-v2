package Message.Game;

import Message.*;

import java.util.UUID;

public class GameJoinMessage extends Message {


  public GameJoinMessage(String sender, String recipient) {
    super(sender, recipient, Type.GAME_JOIN);
  }

  public GameJoinMessage(UUID identifier, String sender, String recipient) {
    super(identifier, sender, recipient, Type.GAME_JOIN);
  }

}
