package Games.Blackjack;

import Games.GameMessage;
import java.util.Date;

public class BlackjackMessage extends GameMessage<BlackjackMove> {

  public BlackjackMessage(String sender, String recipient, BlackjackMove move, Date date) {
    super(sender, recipient, move, date);
  }
}
