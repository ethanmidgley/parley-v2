package Games;

import Message.*;
import MessageQueue.MessageQueue;
import ConnectedClient.ConnectedClient;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class GameClient<TGameUpdate,TGameMessage extends GameMessage> extends ConnectedClient {


  public GameEngine engine;
  private BlockingQueue<TGameUpdate> queue;

  public GameClient(String identifier, MessageQueue mq, GameEngine engine) {
    super(identifier, mq);
    this.queue = new LinkedBlockingQueue<TGameUpdate>();
    this.engine = engine;
    this.engine.bindUpdateQueue(queue);
  }

  @Override
  public void listen() {

    for (;;) {

      TGameUpdate update = queue.poll();
      dispatch(handleUpdate(update));


    }

  }

  protected abstract TGameMessage handleUpdate(TGameUpdate state);


  @Override
  public void send(Message message) {

    if (message.getType() == Type.GAME_JOIN) {

      try {
        engine.join(message.getSender());
      } catch (GameFullException e) {
        super.dispatch(message.reply("Game is full"));
      }

    }

    try {

      TGameMessage msg = (TGameMessage) message;
      engine.handleMove(msg.getGameMove());

    } catch (ClassCastException e) {

      super.dispatch(message.reply("Unable to parse move"));

    }

  }
}
