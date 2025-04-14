package Games;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public abstract class GameEngine<GameUpdate, GameMove> {

  // represents everyone playing the game using their identifiers
  public final ArrayList<String> players;
  private final int CAPACITY;
  private BlockingQueue<GameUpdate> updateQueue;

  public GameEngine() {
    players = new ArrayList<>();
    CAPACITY = 5;
  }

  public GameEngine(int CAPACITY) {
    players = new ArrayList<>();
    this.CAPACITY = CAPACITY;
  }

  public boolean join(String identifier) throws GameFullException {

    if (players.size() >= CAPACITY) {
      throw new GameFullException("game is at capacity");
    }

    players.add(identifier);
    return true;

  }

  public boolean leave(String identifier) {
    return players.remove(identifier);
  }


  public void pushUpdate(GameUpdate update) {
    if (updateQueue == null) {
      throw new RuntimeException("updates queue is not bound");
    }
    updateQueue.offer(update);
  }

  public void bindUpdateQueue(BlockingQueue<GameUpdate> updateQueue) {
    this.updateQueue = updateQueue;
  }

  public abstract void handleMove(GameMove gameMove) throws IllegalMoveException;

}
