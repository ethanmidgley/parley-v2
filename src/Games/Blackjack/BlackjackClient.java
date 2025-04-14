package Games.Blackjack;

import Games.GameClient;
import Message.*;
import MessageQueue.MessageQueue;


// the instantiation of a game client will return a identifier that it points to in the client directory
// in the gui the user can then click join game, in which a popup will appear they can enter the code
// Which will run the game in another window

// When we create a game we need to return info to the client so they can display the relevant window

// This should definitely be abstracted further in to possibly a game connected client therefore the joining logic is not repeated for all games
// But probably shouldn't take it any further otherwise kinda ruins the controller service model
public class BlackjackClient extends GameClient<BlackjackUpdate, BlackjackMessage> {

  public BlackjackClient(String identifier, MessageQueue mq) {
    super(identifier, mq, new BlackjackEngine());
  }

  @Override
  protected BlackjackMessage handleUpdate(BlackjackUpdate update) {
    return null;
  }


//  private BlackjackEngine engine;
//
//  public BlackjackClient(String identifier, MessageQueue mq) {
//    super(identifier, mq);
//    this.engine = new BlackjackEngine();
//  }

}