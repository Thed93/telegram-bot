package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.DateInThePastException;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.service.TelegramBotService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private static final Pattern PATTERN = Pattern.compile(
            "([0-9.:\\s]{15,16}) ([А-яA-z\\d,\\s.?!:;&*#()$%/+-]+)");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");

    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;
    private final TelegramBotService telegramBotService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService, TelegramBotService telegramBotService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
        this.telegramBotService = telegramBotService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this); //initiate checking for updates at 100 milliseconds by default
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Message message = update.message();
            Long chatId = message.chat().id();
            LocalDateTime dateTime;
            if (update.message() != null && message.text() != null) {
                String userName = message.chat().firstName();
                String text = message.text();
                Matcher matcher = PATTERN.matcher(text);
                if (text.equals("/start")) {
                    telegramBotService.sendMessage(chatId,
                            userName + "! Тестовый бот приветствует вас!\n" +
                                    "Для планирования задачи отправьте её в формате:\n" +
                                    "*01.01.2023 20:00 Сделать домашнюю работу*",
                            ParseMode.Markdown);
                } else if (matcher.matches() &&
                        (dateTime = parseLocalDateTime(matcher.group(1))) != null ){
                    try {
                        notificationTaskService.saveNotification(matcher.group(2), dateTime, chatId);
                        telegramBotService.sendMessage(chatId,userName + ", ваша задача успешно запланирована!");
                    } catch (DateInThePastException e) {
                        telegramBotService.sendMessage(chatId,e.getMessage());
                    }
                }  else if (matcher.matches() && parseLocalDateTime(matcher.group(1)) == null) {
                    telegramBotService.sendMessage(chatId, "Формат даты и времени не верный. Попробуйте снова.");
                    logger.warn("Date and time format is not correct in " + chatId + " chat.");
                }
                else {
                    telegramBotService.sendMessage(
                            chatId,
                            userName + ", пока не знаю ответа! Чтобы вернуться к началу, отправьте /start");
                    logger.warn("Unrecognized message in " + chatId + " chat.");
                }
            } else {
                telegramBotService.sendMessage(chatId,
                        "Отправьте команду /start или сообщение для планирования задачи!");
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL; // return id of last processed update or confirm them all
    }

    @Nullable
    private LocalDateTime parseLocalDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }



}