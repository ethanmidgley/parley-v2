package Client.Components.Games;

import Client.Client;
import Client.ClientState;
import Games.Blackjack.BlackjackState;
import Message.Game.GameType;

public class GameWindowFactory {

  public static <T> GameWindow createGameWindow(GameType type, String game_id, Client client, ClientState state, T gameState) {

    return switch (type) {
      case BLACKJACK ->  new BlackJackWindow(game_id, type.name(), client, state, (BlackjackState) gameState);
    };

  }


}
