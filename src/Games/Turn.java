package Games;

import java.io.Serializable;
import java.util.List;

public class Turn<TPlayer, TAction> implements Serializable {
  public TPlayer player;
  public List<TAction> validActions;

  public Turn(TPlayer player, List<TAction> validActions) {
    this.player = player;
    this.validActions = validActions;
  }

}
