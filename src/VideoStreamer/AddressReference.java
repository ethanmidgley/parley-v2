package VideoStreamer;

import java.net.InetAddress;

public class AddressReference {
  private InetAddress address;

  public AddressReference(InetAddress address) {
    this.address = address;
  }

  public InetAddress getAddress() {
    return address;
  }

  public void setAddress(InetAddress address) {
    this.address = address;
  }

}
