package Message;

import java.util.Date;
import java.util.UUID;

public class SignalMessage extends Message {

  private boolean accepted;
  private SignalType signalType;

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  private String address;

  public SignalMessage(String sender, String recipient, SignalType signalType) {
    super(sender, recipient, Type.SIGNAL);
    this.signalType = signalType;
    this.accepted = false;
  }

  public SignalMessage(UUID identifier, String sender, String recipient, SignalType signalType) {
    super(identifier, sender, recipient, Type.SIGNAL);
    this.signalType = signalType;
    this.accepted = false;
  }

  public SignalMessage(UUID identifier, String sender, String recipient, SignalType signalType, boolean acknowledge) {
    super(identifier, sender, recipient, acknowledge ? Type.SIGNAL_ACK : Type.SIGNAL);
    this.signalType = signalType;
  }

  public SignalMessage reply(boolean accepted) {
    SignalMessage response = new SignalMessage(this.getId(), this.getRecipient(), this.getSender(),this.signalType, true);
    response.setAccepted(accepted);
    return response;
  }

  public boolean isAccepted() {
    return accepted;
  }

  public void setAccepted(boolean accepted) {
    this.accepted = accepted;
  }

  public SignalType getSignalType() {
    return signalType;
  }

}
