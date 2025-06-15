package Client.ViewableMessage;

import Message.Message;
import Message.TextMessage;

import javax.swing.*;
import java.awt.*;

public class ViewableTextMessage implements ViewableMessage {

  private TextMessage message;
  private JLabel chatline;

  public ViewableTextMessage(TextMessage message) {

    this.message = message;

    this.chatline = new JLabel(message.getSender() + ": " + message.getContent());
    this.chatline.setFont(new Font("Arial", Font.PLAIN, 20));


  }


  public java.awt.Component render() {
    return chatline;
  }

  @Override
  public Message underlyingMessage() {
    return this.message;
  }
}
