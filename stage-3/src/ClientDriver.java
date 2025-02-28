public class ClientDriver {

  public static void main(String[] args) {

    ClientState state = new ClientState();

    Gui gui = new Gui();

    Client client = new Client((Message message) -> {
        if (message.getType() == Type.TEXT) {
          state.addMessageBySender(message);
//          System.out.printf("\033[2K\r%s: %s\n", message.getSender(), message.getContent());
        } else {
          System.out.println("\033[2K\rError - Received incorrect message type");
        }
    });
  }

}
