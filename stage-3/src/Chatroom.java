import java.util.ArrayList;

public class Chatroom {

  private final ClientDirectory directory;
  Chatroom(ClientDirectory directory){
    this.directory = directory;
  }

  public void dispatch(Message message){
    ArrayList<ConnectedClient> client_list;
    client_list = directory.values();
    for (ConnectedClient client : client_list){
      Message new_message = new Message(message.getSender(), client.getName(), message.getContent(), message.getSendDate(), message.getType());
      client.send(new_message);
    }
  }

}