package pro.sky.telegrambot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "notification_tasks")
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 4096, nullable = false)
    private String text;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    public Long getChatId() {
        return chatId;
    }

    @Column(name = "chat_id", nullable = false)
    private Long chatId;


    public NotificationTask(String text, LocalDateTime dateTime, Long chatId) {
        this.text = text;
        this.dateTime = dateTime;
        this.chatId = chatId;
    }

    public NotificationTask() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }


}
