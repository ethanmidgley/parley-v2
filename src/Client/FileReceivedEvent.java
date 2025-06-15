package Client;

import Client.ViewableMessage.ViewableFileMessage;

public interface FileReceivedEvent {
  void trigger(ViewableFileMessage file);
}
