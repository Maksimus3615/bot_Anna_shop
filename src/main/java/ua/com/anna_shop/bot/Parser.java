package ua.com.anna_shop.bot;

import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import org.springframework.stereotype.Service;
import ua.com.anna_shop.models.Order;
import ua.com.anna_shop.models.User;

@Service
public class Parser implements TradeConstants {

  public Order getOrder(User user) {
    String textFromUser = user.getTextFromUser();
    textFromUser = textFromUser.replace(";", ",");
    Order order = new Order()
        .setCustomer(getCustomerName(textFromUser))
        .setOrderDetails(getOrderDetails(textFromUser).toString())
        .setQuantity(getOrderDetails(textFromUser).size())
        .setSalary(getOrderDetails(textFromUser).size() * SALARY)
        .setPrepayment(getPrepayment(textFromUser))
        .setCashOnDelivery(getCashOnDelivery(textFromUser));
    if (order.getPrepayment() != 0 && order.getCashOnDelivery() != 0) {
      order.setTotalCost(order.getPrepayment() + order.getCashOnDelivery() - 10);
    } else {
      order.setPrepayment(getTotalCost(textFromUser));
      order.setTotalCost(getTotalCost(textFromUser));
    }
    return order;
  }

  private String getCustomerName(String text) {

    StringBuilder customerName = new StringBuilder();
    String[] parts = text.split("\n");

    for (String part : parts) {
      if (!isStringEmpty(part)) {
        customerName.append(part);
        customerName.append(" ");
      } else {
        break;
      }
    }
    return customerName.toString();
  }

  private ArrayList<String> getOrderDetails(String text) {
    ArrayList<String> orders = new ArrayList<>();
    String[] parts = text.split("\n");
    boolean isOrder = false;
    for (String part : parts) {
      if (isStringEmpty(part)) {
        isOrder = true;
      } else if (isOrder
          && !part.contains(PREPAYMENT)
          && !part.contains(CASH_ON_DELIVERY)
          && !part.contains("Дроп")
          && !part.contains("Цена")) {
        orders.add(part);
      }
    }
    return orders;
  }

  private int getPrepayment(String text) {
    int prepayment = 0;

    String[] parts = text.split("\n");
    for (String part : parts) {
      if (part.contains(PREPAYMENT)) {
        prepayment = getNumber(part);
      }
    }
    return prepayment;
  }

  private int getTotalCost(String text) {
    int cost = 0;

    String[] parts = text.split("\n");
    for (String part : parts) {
      if (part.contains("Цена")) {
        cost = getNumber(part);
      }
    }
    return cost;
  }

  private int getCashOnDelivery(String text) {
    int cash = 0;

    String[] parts = text.split("\n");
    for (String part : parts) {
      if (part.contains(CASH_ON_DELIVERY)) {
        cash = getNumber(part);
      }
    }
    return cash;
  }

  /**
   * Проверяет пустая строка или нет.
   *
   * @param someString - просто строка;
   * @return если в строке нет цифр или букв - то true.
   */
  private boolean isStringEmpty(String someString) {
    for (int j = 0; j < someString.length(); j++) {
      if (Character.isLetter(someString.charAt(j)) ||
          Character.isDigit(someString.charAt(j))) {
        return false;
      }
    }
    return true;
  }

  private int getNumber(String someString) {
    int number = 0;
    StringBuilder numeric = new StringBuilder();
    char[] symbols = someString.toCharArray();
    for (char symbol : symbols) {
      if (Character.isDigit(symbol)) {
        numeric.append(symbol);
      }
    }
    if (!numeric.toString().equals("")) {
      number = parseInt(numeric.toString());
    }
    return number;
  }
}
