package ua.com.anna_shop.models;

import org.telegram.telegrambots.meta.api.objects.Message;

public class User {
  Long userId;
  String userFirstName;
  String userLastName;
  String textFromUser;

  public User(Message message) {
    this.userId = message.getChatId();
    this.userFirstName = message.getFrom().getFirstName();
    this.userLastName = "";
    if (message.getFrom().getLastName() != null) {
    this.userLastName = message.getFrom().getLastName();
    }
    this.textFromUser = message.getText();
  }

  public Long getUserId() {
    return userId;
  }

  public String getUserFirstName() {
    return userFirstName;
  }

  public String getUserLastName() {
    return userLastName;
  }

  public String getTextFromUser() {
    return textFromUser;
  }
}
