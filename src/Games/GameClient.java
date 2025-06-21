package Games;

import ClientDirectory.ClientDirectory;
import Message.*;
import Message.Game.*;
import MessageQueue.MessageQueue;
import ConnectedClient.ConnectedClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class GameClient<TGameMove, TGameState extends GameState<TGameState>> extends ConnectedClient {


  public GameEngine engine;
  private BlockingQueue<TGameState> gameStateQueue;
  private GameType gameType;
  private final ClientDirectory directory;

  public GameClient(String identifier, ClientDirectory directory, MessageQueue mq, GameEngine engine, GameType gameType) {
    super(identifier, mq);
    this.gameType = gameType;
    this.directory = directory;
    this.gameStateQueue = new LinkedBlockingQueue<TGameState>();
    this.engine = engine;
    this.engine.bindStateQueue(gameStateQueue);
    this.directory.add(identifier, this);
  }

  @Override
  public void listen() {

    for (;;) {

      try {
        TGameState gameState = gameStateQueue.take();

        List<ConnectedClient> players = engine.getPlayers();
        players.stream()
                .map(p -> new GameStateMessage<TGameState>(super.getIdentifier(), p.getIdentifier(),gameState.deepCopy()))
                .forEach(super::dispatch);
      } catch (InterruptedException e) {
        return;
      }

    }

  }

  @Override
  public void send(Message message) {

    if (message.getType() == Type.GAME_JOIN) {

      GameJoinMessage gameJoinMessage = (GameJoinMessage) message;

      try {


        ConnectedClient new_player = this.directory.get(gameJoinMessage.getSender());
        engine.join(new_player);

        // We will send a message to every one in the game. and the instantiate message to the person who just joined
        ArrayList<ConnectedClient> players = engine.players;
        players.stream()
          .map(player -> player.getIdentifier().equals(gameJoinMessage.getSender()) ?
            new GameCreateMessage(gameJoinMessage.getId(), super.getIdentifier(), player.getIdentifier(), this.gameType, true)
            : new TextMessage(super.getIdentifier(), player.getIdentifier(), gameJoinMessage.getSender() + " joined the game", Type.GAME_NOTIFICATION)
          ).forEach(super::dispatch);

      } catch (GameFullException e) {
        Message ms = gameJoinMessage.errorReply("Game is full");
        super.dispatch(ms);

      }

    }

    if (message.getType() == Type.GAME_LEAVE) {
      // We would need to a boot out if the game is empty
      ConnectedClient player = directory.get(message.getSender());
      engine.leave(player);
      if (engine.players.size() == 0) {
        // If the game is now empty lets leave the lobby
        this.directory.remove(super.getIdentifier());
        // Hopefully stop running the thread running this game
        this.interrupt();
      }
    }

    if (message.getType() == Type.GAME_MOVE) {
      try {

        GameMoveMessage<TGameMove> gameMoveMessage = (GameMoveMessage<TGameMove>) message;
        ConnectedClient player = directory.get(gameMoveMessage.getSender());
        try {
          engine.handleMove(player, gameMoveMessage.getGameMove());
        } catch (IllegalMoveException e) {

          Message ms = message.errorReply("Illegal move");
          super.dispatch(ms);
        }

      } catch (ClassCastException e) {

        super.dispatch(message.errorReply("Unable to parse move"));

      }

    }

    if (message.getType() == Type.GAME_START) {
      engine.startGame();
    }

  }
}
