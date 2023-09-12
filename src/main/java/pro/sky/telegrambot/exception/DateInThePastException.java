package pro.sky.telegrambot.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateInThePastException extends RuntimeException {
    private final LocalDateTime localDateTime;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public DateInThePastException(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String getMessage() {
        return localDateTime.format(DATE_TIME_FORMATTER)  + " - указанная дата и время уже прошли!";
    }
}
