package MessageConsumer;

import ClientDirectory.*;
import ConnectedClient.ConnectedClient;
import Message.*;
import MessageQueue.MessageQueue;
import java.util.Date;

public class MessageConsumer implements Runnable{
  private final MessageQueue mq;
  private final MessageQueue loggerQ;
  private final ClientDirectory directory;

  public MessageConsumer(ClientDirectory directory, MessageQueue logQ, MessageQueue mq) {
    this.directory = directory;
    this.loggerQ = logQ;
    this.mq = mq;
  }


  private void consume() {
    for (;;) {
      Message m = this.mq.poll();

      if (m == null) {
        continue;
      }

      switch (m.getType()) {

        case USERNAME_PROPAGATE, UPDATE_USERNAME -> {

          Message response;

          try {

            ConnectedClient client = directory.update(m.getSender(), m.getContent());
            client.setIdentifier(m.getContent());
            response = m.reply("Username successfully set");

          } catch (IdentifierTakenException e) {
            response = m.reply("Username already taken");
          } catch (IdentifierNotFoundException e) {
            response = m.reply("Could not find you");
          }

          sendMessage(response);

        }

        case TEXT -> {
          loggerQ.offer(m);
          sendMessage(m);
        }

        case SIGNAL -> {
          sendMessage(m);
        }

        case SIGNAL_ACK -> {

          String[] arr = m.getContent().split(":");
          if (arr[0].equals("Accepted ")){ // if the user accepted the request

            ConnectedClient client = directory.get(m.getSender());
            Message success_message = new Message(m.getSender(), m.getRecipient(), client.getInetAddress().getHostAddress() + ":" + arr[1], new Date(), Type.SIGNAL_ACK);
            sendMessage(success_message);

          } else {
//            TODO: FINISH THIS IT SHOULD REPLY TO THE OLD SIGNAL MESSAGE REALLY
//            TODO: GOING TO BE ROUGH
//            Message denial_message = new Message(input.getSender(), input.getRecipient(), "denied your" + arr[1], new Date(), Type.TEXT);
//            super.dispatch(denial_message);
//            Message denial_message_from = new Message(input.getRecipient(), input.getSender(), "sent a" + arr[1] + " that you denied", new Date(), Type.TEXT);
//            super.dispatch(denial_message_from);
          }

        }








      }










    }
  }

  public void sendMessage(Message message) {

      ConnectedClient client = this.directory.get(message.getRecipient());

      if (client == null) {
        Message error_message = message.reply( "Recipient not found");
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
