package Games.Blackjack;

import ConnectedClient.ConnectedClient;
import Message.*;
import MessageQueue.MessageQueue;
import OnlineCount.OnlineCount;

import java.util.ArrayList;


// the instantiation of a game client will return a identifier that it points to in the client directory
// in the gui the user can then click join game, in which a popup will appear they can enter the code
// Which will run the game in another window

// When we join a game we need to return info to the client so they can display the relevant window

// This should definitely be abstracted further in to possibly a game connected client therefore the joining logic is not repeated for all games
// But probably shouldn't take it any further otherwise kinda ruins the controller service model
public class BlackjackClient extends ConnectedClient {

  private final ArrayList<ConnectedClient> players;
  private final static int GAME_CAPACITY = 5;

  public BlackjackClient(String identifier, MessageQueue mq, OnlineCount onlineCount) {
    super(identifier, mq);
    this.players = new ArrayList<>();
  }

  @Override
  public void listen() {

  }

  @Override
  public void send(Message message) {

    if (message.getType() == Type.GAME_JOIN) {




    }



    try {

      BlackjackMessage msg = (BlackjackMessage) message;



    } catch (ClassCastException e) {

    }


  }
}