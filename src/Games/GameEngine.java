package Games;

import ConnectedClient.ConnectedClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public abstract class GameEngine<TGameMove, TInternalGameState, TGameState> {

  // represents everyone playing the game using their identifiers
  public final ArrayList<ConnectedClient> players;
  private final int CAPACITY;
  private BlockingQueue<TGameState> stateQueue;

  public GameEngine() {
    players = new ArrayList<>();
    CAPACITY = 5;
  }

  public GameEngine(int CAPACITY) {
    players = new ArrayList<>();
    this.CAPACITY = CAPACITY;
  }

  public abstract void handlePlayerJoin(ConnectedClient player);
  public abstract void handlePlayerLeave(ConnectedClient player);
  public abstract void handleMove(ConnectedClient player, TGameMove gameMove) throws IllegalMoveException;
  public abstract void startGame();
  public abstract TGameState getState();
  public abstract TGameState convert(TInternalGameState internalGameState);

  public boolean join(ConnectedClient player) throws GameFullException {

    if (players.size() >= CAPACITY) {
      throw new GameFullException("game is at capacity");
    }

    players.add(player);
    handlePlayerJoin(player);
    return true;

  }


  public boolean leave(ConnectedClient player) {
    handlePlayerLeave(player);
    return players.remove(player);
  }

  public List<ConnectedClient> getPlayers() {
    return this.players;
  }


  public void pushState(TInternalGameState state) {
    if (stateQueue == null) {
      throw new RuntimeException("updates queue is not bound");
    }
    stateQueue.offer(convert(state));
  }

  public void bindStateQueue(BlockingQueue<TGameState> stateQueue) {
    this.stateQueue = stateQueue;
  }

}