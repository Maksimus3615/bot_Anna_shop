package ua.com.anna_shop.bot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.com.anna_shop.models.Order;
import ua.com.anna_shop.models.User;
import ua.com.anna_shop.repo.Repo;


@Component
public class Bot extends TelegramLongPollingBot implements TradeConstants {

    private final Logger log = LoggerFactory.getLogger(Bot.class);

    private final Repo repo;

    private final Parser parser;

    public Bot(Repo repo, Parser parser) {
        this.repo = repo;
        this.parser = parser;
    }

    private final HashMap<Long, String> currentShopOfUser = new HashMap<>();
    private final HashMap<String, Integer> userSessions = new HashMap<>();

    private String loggerMessage = "";

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.user.name}")
    private String botName;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (update.hasMessage() && update.getMessage().hasText()) {
            User user = new User(message);
            switch (user.getTextFromUser()) {
                case START_BUTTON_NAME:
                    if (currentShopOfUser.containsKey(user.getUserId()))
                        botStart(user);
                    else {
                        String text = "Зайдите в магазин для начала работы...";
                        botSendMessage(user, text);
                    }
                    break;
                case SHOP_NAME_ANNA:
                    botSendMessage(user, "Магазин АННА приветсвует Вас!");
                    currentShopOfUser.put(user.getUserId(), SHOP_NAME_ANNA);
                    loggerMessage = "Магазины с продавцом: " + currentShopOfUser;
                    log.info(loggerMessage);
                    break;
                case SHOP_NAME_VENERA:
                    botSendMessage(user, "Магазин ВЕНЕРА приветсвует Вас!");
                    currentShopOfUser.put(user.getUserId(), SHOP_NAME_VENERA);
                    loggerMessage = "Магазины с продавцом: " + currentShopOfUser;
                    log.info(loggerMessage);
                    break;
                case STOP_BUTTON_NAME:
                    botStop(user);
                    break;
                case "12345":
                    botSendMessage(user, "Бот остановлен...");
                    System.exit(123459);
                    break;
                default:
                    if (userSessions.containsKey(getSessionName(user))) {
                        botSaveOrder(user);
                    } else {
                        String text = "Нажмите кнопку " + START_BUTTON_NAME + " для начала рабочей сессии!";
                        botSendMessage(user, text);
                    }
                    break;
            }
        } else {
            log.error("Something went wrong: unexpected message format could not be processed.");
        }
    }

    private void botSendMessage(User user, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(START_BUTTON_NAME);
        keyboardFirstRow.add(STOP_BUTTON_NAME);
        keyboardSecondRow.add(SHOP_NAME_ANNA);
        keyboardSecondRow.add(SHOP_NAME_VENERA);
        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendMessage.setChatId(user.getUserId());
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void botStart(User user) {
        LocalDateTime startTime = LocalDateTime.now();
        Integer userSessionHashCode = startTime.hashCode() + user.getUserId().hashCode();
        userSessions.put(getSessionName(user), userSessionHashCode);

        loggerMessage = startTime
                + ": продавец "
                + user.getUserFirstName() + " "
                + user.getUserLastName()
                + " начал работу в магазине "
                + getCurrentShopOfUser(user);
        log.info(loggerMessage);

        botSendMessage(user, loggerMessage);

        loggerMessage = "   код сессии: " + userSessionHashCode;
        log.info(loggerMessage);
        loggerMessage = "   код продавца: " + user.getUserId();
        log.info(loggerMessage);
        loggerMessage = "   Открытые сессии: " + userSessions;
        log.info(loggerMessage);
        loggerMessage = "   Магазины с продавцом: " + currentShopOfUser;
        log.info(loggerMessage);
    }

    private void botStop(User user) {
        sendReport(user);
        String text = "магазин "
                + getCurrentShopOfUser(user)
                + " закрыт! "
                + user.getUserFirstName()
                + ", спасибо за труд и хороших выходных...";
        botSendMessage(user, text);

        loggerMessage = LocalDateTime.now()
                + ": продавец "
                + user.getUserFirstName() + " "
                + user.getUserLastName()
                + " закончил работу...";
        log.info(loggerMessage);

        userSessions.remove(getSessionName(user));
        currentShopOfUser.remove(user.getUserId());

        loggerMessage = "   Открытые сессии: " + userSessions;
        log.info(loggerMessage);
        loggerMessage = "   Магазины с продавцом: " + currentShopOfUser;
        log.info(loggerMessage);
    }

    private void sendReport(User user) {
        try {
            execute(getDocument(user));
            log.info("Отчет отправлен...");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendDocument getDocument(User user) {
        String fileName = getCurrentShopOfUser(user) + LocalDate.now() + ".csv";
        File fileCSV = new File(fileName);
        ArrayList<Order> orders = getAllOrders(getUserSessionHashCode(user));

        try (PrintWriter writer = new PrintWriter(fileCSV, "Cp1251");
             BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write("НОМЕР;КЛИЕНТ;ТОВАР;НА КАРТУ;СТОИМОСТЬ;ЗП;");
            bw.newLine();
            int counter = 0;
            int salary = 0;
            int prePayment = 0;
            int quantity = 0;
            for (Order order : orders) {
                salary += order.getSalary();
                prePayment += order.getPrepayment();
                quantity += order.getQuantity();
                bw.write(++counter + ";" +
                        order.getCustomer() + ";" +
                        order.getOrderDetails() + ";" +
                        order.getPrepayment() + ";" +
                        order.getTotalCost() + ";" +
                        order.getSalary() + ";");
                bw.newLine();
                deleteOrders(order.getId());
            }
            bw.write("ИТОГО:;;" + quantity + ";" + prePayment + ";;" + salary + ";");
            bw.newLine();
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        InputFile file = new InputFile(fileCSV);
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(user.getUserId());
        sendDocument.setDocument(file);

        return sendDocument;
    }

    private void botSaveOrder(User user) {
        Order order = parser.getOrder(user)
                .setUserSessionHashCode(getUserSessionHashCode(user));
        if (order.getPrepayment() == 0 && order.getTotalCost() == 0) {
            botSendMessage(user, "ОШИБКА: неправильно указан объем платежей.");
        } else {
            repo.save(order);
            botSendMessage(user, BOT_RESPONSE
                    + " Вы торгуете в магазине "
                    + getCurrentShopOfUser(user));
        }
        loggerMessage = user.getUserFirstName()
                + " добавила заказ в магазин "
                + getCurrentShopOfUser(user);
        log.info(loggerMessage);
        loggerMessage = "   Заказчик: " + order.getCustomer();
        log.info(loggerMessage);
        loggerMessage = "   Детали заказа: " + order.getOrderDetails();
        log.info(loggerMessage);
        loggerMessage = "   Количество: " + order.getQuantity();
        log.info(loggerMessage);
        loggerMessage = "   Предоплата: " + order.getPrepayment();
        log.info(loggerMessage);
        loggerMessage = "   Наложенный платеж: " + order.getCashOnDelivery();
        log.info(loggerMessage);
        loggerMessage = "   Сумма заказа: " + order.getTotalCost();
        log.info(loggerMessage);
        loggerMessage = "   зарплата:" + order.getSalary();
        log.info(loggerMessage);
    }

    private String getCurrentShopOfUser(User user) {
        return currentShopOfUser.get(user.getUserId());
    }

    private String getSessionName(User user) {
        return user.getUserId() + getCurrentShopOfUser(user);
    }

    private Integer getUserSessionHashCode(User user) {
        return userSessions.get(getSessionName(user));
    }

    private ArrayList<Order> getAllOrders(Integer userSessionHashCode) {
        return repo.findAllByUserSessionHashCode(userSessionHashCode);
    }

    private void deleteOrders(Long id) {
        repo.deleteById(id);
    }
}
