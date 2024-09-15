package pro.sky.telegrambot.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.TelegramBotService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class NotificationTaskNotifier {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTaskNotifier.class);
    private final TelegramBotService telegramBotService;

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskNotifier(TelegramBotService telegramBotService, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBotService = telegramBotService;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Scheduled(cron = "* * * * * *") //Cron format for every minute
    @Transactional
    public void task(){
        notificationTaskRepository.findAllByDateTime(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES) //Returns a copy of this LocalTime truncated to minutes
        ).forEach(notificationTask -> {
            telegramBotService.sendMessage(notificationTask.getChatId(),
                    "Вы просили напомнить: " + notificationTask.getText());
            notificationTaskRepository.delete(notificationTask);
            logger.info("Notification for "+ notificationTask.getChatId() + " chat sent and deleted from the repository");
        });

    }




}