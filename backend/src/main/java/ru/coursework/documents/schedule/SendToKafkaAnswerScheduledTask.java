package ru.coursework.documents.schedule;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.coursework.documents.entity.OutboxMessageEntity;
import ru.coursework.documents.kafka.KafkaSender;
import ru.coursework.documents.repository.OutboxMessageRepository;

import java.util.List;

/**
 * Периодическая отправка сообщений с ответами в кафку
 */
@Component
@AllArgsConstructor
public class SendToKafkaAnswerScheduledTask {
    private static final long TIME_RATE = 1000;
    private static final String KAFKA_TOPIC_NAME = "answer";
    private final KafkaSender kafkaSender;
    private final OutboxMessageRepository outboxMessageRepository;

    @Scheduled(fixedRate = TIME_RATE)
    public void sendMessagesToKafka() {
        List<OutboxMessageEntity> messages = outboxMessageRepository.findBySendFalse();
        messages.forEach((message) -> {
            kafkaSender.sendMessage(message.getMessage(), message.getId().toString(), KAFKA_TOPIC_NAME);
            message.setSend(true);
            outboxMessageRepository.save(message);
        });
    }
}
