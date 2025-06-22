package MessageConsumer;

import ClientDirectory.ClientDirectory;
import ClientDirectory.IdentifierNotFoundException;
import ClientDirectory.IdentifierTakenException;
import ConnectedClient.ConnectedClient;
import ConnectedClient.DependentClient;
import Games.Blackjack.BlackjackClient;
import Games.GameFullException;
import Message.*;
import Message.Game.GameCreateMessage;
import MessageQueue.MessageQueue;

import java.util.*;

public class MessageConsumer implements Runnable {
  private final MessageQueue mq;
  private final MessageQueue loggerQ;
  private final ClientDirectory directory;
  private final Map<Type, MessageCallback> callbacks;

  private final Context ctx;


  public MessageConsumer(ClientDirectory directory, MessageQueue logQ, MessageQueue mq) {
    this.directory = directory;
    this.loggerQ = logQ;
    this.mq = mq;

    this.ctx = new Context(directory, logQ, mq);


    this.callbacks = new HashMap<Type, MessageCallback>();

    this.callbacks.put(Type.TEXT, (MessageCallback<TextMessage>) (Request<TextMessage> req) -> {

      Context ctx = req.getCtx();
      MessageQueue loggerQ = ctx.getLoggerQ();

      loggerQ.offer(req.getMessage());

      return Arrays.asList(req.getMessage());

    });

    MessageCallback<? extends Message> simpleSend = (Request<Message> req) -> Collections.singletonList(req.getMessage());


    // GAMES
    this.callbacks.put(Type.GAME_MOVE, simpleSend);
    this.callbacks.put(Type.GAME_JOIN, simpleSend);
    this.callbacks.put(Type.GAME_INVITE, simpleSend);
    this.callbacks.put(Type.GAME_LEAVE, simpleSend);
    this.callbacks.put(Type.GAME_JOIN_SUCCESS, simpleSend);
    this.callbacks.put(Type.GAME_NOTIFICATION, simpleSend);
    this.callbacks.put(Type.GAME_STATE, simpleSend);

    this.callbacks.put(Type.ERROR, simpleSend);

    this.callbacks.put(Type.GAME_START, simpleSend);

    this.callbacks.put(Type.GAME_INSTANTIATE, (MessageCallback<GameCreateMessage>) (Request<GameCreateMessage> req) -> {

      GameCreateMessage msg = req.getMessage();
      Context ctx = req.getCtx();
      ClientDirectory d = ctx.getDirectory();

      switch (msg.getGameType()) {

        case BLACKJACK:

          String gameIdentifier = UUID.randomUUID().toString();

          BlackjackClient blackjackClient =  new BlackjackClient(gameIdentifier, d, ctx.getMessageQ());
          blackjackClient.start();

          Message result;

          try {

            ConnectedClient player = d.get(msg.getSender());
            blackjackClient.engine.join(player);
            player.addDependent(blackjackClient);
            result = new GameCreateMessage(msg.getId(), gameIdentifier, msg.getSender(), msg.getGameType(), blackjackClient.engine.getState());

          } catch (GameFullException e) {
            result = msg.errorReply("Game full");
          }

          return Arrays.asList(result);

      }

      return null;

    });


    // USERNAME UPDATES
    MessageCallback<UsernameMessage> usernameCallback = (Request<UsernameMessage> req) -> {

      Context ctx = req.getCtx();
      ClientDirectory d = ctx.getDirectory();
      UsernameMessage m = req.getMessage();
      Message response;

      try {

        ConnectedClient client = d.update(m.getSender(), m.getUsername());
        client.setIdentifier(m.getUsername());
        response = m.successReply("Username updated");


      } catch (IdentifierTakenException e) {
        response = m.errorReply("Username already taken");
      } catch (IdentifierNotFoundException e) {
        response = m.errorReply("Could not find you");
      }

      return Arrays.asList(response);

    };

    this.callbacks.put(Type.USERNAME_PROPAGATE, usernameCallback);
    this.callbacks.put(Type.USERNAME_UPDATE, usernameCallback);


    // SIGNALS
    this.callbacks.put(Type.SIGNAL, simpleSend);
    this.callbacks.put(Type.SIGNAL_ACK, (MessageCallback<SignalMessage>) (Request<SignalMessage> req) -> {

      SignalMessage msg = req.getMessage();
      Context ctx = req.getCtx();
      ClientDirectory d = ctx.getDirectory();

      if (msg.isAccepted()) {
        msg.setAddress(d.get(msg.getSender()).getInetAddress().getHostAddress());
      }
      return List.of(msg);

    });


    // CHATROOMS
    this.callbacks.put(Type.CHATROOM, (MessageCallback<TextMessage>) (Request<TextMessage> req) -> {

      Context ctx = req.getCtx();
      TextMessage msg = req.getMessage();
      ClientDirectory d = ctx.getDirectory();

      ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online

      List<TextMessage> msgs = client_list.stream().filter((String recipient) -> {
        ConnectedClient client = d.get(recipient);
        return !client.isVirtualClient() && msg.getSender() != recipient;
      }).map((String recipient) -> new TextMessage(msg.getSender(), recipient, msg.getContent(), Type.CHATROOM) // create a new message with chatroom enum
      ).toList();

      return msgs;

    });

    // ONLINE USER COUNT
    this.callbacks.put(Type.ONLINE_USERS, (MessageCallback<OnlineCountMessage>) (Request<OnlineCountMessage> req) -> {

      OnlineCountMessage msg = req.getMessage();

      ArrayList<String> client_list = new ArrayList<>(directory.keySet()); // gets a list of all users online

      // send an update to everyone
      return client_list.stream().
      map((String recipient) -> new OnlineCountMessage(recipient, msg.getCount()) // create a new message with chatroom enum
      ).toList();

    });


  }


  private void consume() {
    for (; ; ) {
      Message m = this.mq.poll();

      if (m == null) {
        continue;
      }

      MessageCallback callback = this.callbacks.get(m.getType());
      if (callback == null) {
        System.out.println("Cannot handle message " + m.getType());
        continue;
      }
      Request r = new Request(m, this.ctx);
      List<Message> responses = callback.execute(r);
      responses.stream().forEach(this::sendMessage);

    }
  }

  public void sendMessage(Message message) {

    ConnectedClient client = this.directory.get(message.getRecipient());

    if (client == null) {
      Message error_message = message.errorReply("Recipient not found");
      client = directory.get(message.getSender());
      client.send(error_message);
      return;
    }

    client.send(message);

  }


  @Override
  public void run() {
    consume();
  }
}
