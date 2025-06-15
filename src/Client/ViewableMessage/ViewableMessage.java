package Client.ViewableMessage;

import Message.*;

public  interface ViewableMessage<T extends Message> {
  T underlyingMessage();
  java.awt.Component render();
}
