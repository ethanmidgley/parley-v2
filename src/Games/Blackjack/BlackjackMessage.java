package Games.Blackjack;

import Message.*;

import java.util.Date;

public class BlackjackMessage extends Message {

  private final String action;

  public BlackjackMessage(String sender, String recipient, String action, Date date) {
    super(sender, recipient, "", date, Type.GAME_MOVE);
    this.action = action;
  }

}
