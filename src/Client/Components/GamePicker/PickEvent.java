package Client.Components.GamePicker;

import Message.Game.GameType;

public interface PickEvent {
  void onPick(GameType choice);
}