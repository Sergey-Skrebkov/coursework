package ru.template.example.documents.schedule;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.template.example.documents.entity.MessageForKafkaEntity;
import ru.template.example.documents.repository.MessageForKafkaRepository;
import ru.template.example.kafka.KafkaSender;

import java.util.*;

@Component
@AllArgsConstructor
public class SendToKafkaScheduledTask {
    private static final long TIME_RATE = 5000;
    private static final String KAFKA_TOPIC_NAME = "documents";
    private final KafkaSender kafkaSender;
    private final MessageForKafkaRepository messageForKafkaRepository;

    @Scheduled(fixedRate = TIME_RATE)
    public void sendMessagesToKafka(){
        List<MessageForKafkaEntity> messages = messageForKafkaRepository.findBySendFalse();
        messages.forEach((message) ->{
            kafkaSender.sendMessage(message.getMessage(), message.getId().toString(), KAFKA_TOPIC_NAME);
            message.setSend(true);
            messageForKafkaRepository.save(message);
        });
    }
}
