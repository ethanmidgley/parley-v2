package Games;

import java.io.Serializable;

public class GameState<T extends GameState<T>> implements DeepCopyable<T>, Serializable {
  public boolean gameStarted;
  public GameState(boolean gameStarted) {
    this.gameStarted = gameStarted;
  }

  public T deepCopy() {
    throw new UnsupportedOperationException("Override in subclass");
  }

}
