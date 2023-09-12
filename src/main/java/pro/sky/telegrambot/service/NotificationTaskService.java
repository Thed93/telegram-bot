package pro.sky.telegrambot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.exception.DateInThePastException;
import pro.sky.telegrambot.repository.NotificationTaskRepository;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class NotificationTaskService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationTaskService.class);
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Transactional
    public void saveNotification(String text, LocalDateTime dateTime, long chatId) {

        LocalDateTime nowDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        if(dateTime.isAfter(nowDateTime)){
            notificationTaskRepository.save(
                    new NotificationTask(text, dateTime.truncatedTo(ChronoUnit.MINUTES), chatId)
            );
            logger.info("Notification for "+ chatId + " chat saved in the repository");
        } else {
            logger.warn(dateTime + " left in the past");
            throw new DateInThePastException(dateTime);
        }

    }
}