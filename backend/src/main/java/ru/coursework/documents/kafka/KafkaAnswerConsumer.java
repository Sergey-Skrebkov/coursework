package ru.coursework.documents.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.coursework.documents.controller.dto.AnswerDto;
import ru.coursework.documents.entity.MessageForKafkaEntity;
import ru.coursework.documents.repository.MessageForKafkaRepository;
import ru.coursework.documents.service.DocumentService;

import java.util.Optional;
import java.util.UUID;

/**
 * Компонент для приёма ответа из Кафки
 */
@Component
@AllArgsConstructor
public class KafkaAnswerConsumer {
    private final ObjectMapper objectMapper;
    private final DocumentService documentService;
    private final MessageForKafkaRepository messageForKafkaRepository;

    /**
     * Метод слушает топик answer в кафке, при получении
     * @param message текст сообщение
     * @param key ключ сообщения
     * @throws JsonProcessingException если с текстом сообщения что-то не так
     */
    @KafkaListener(topics = "answer", groupId = "group_id")
    public void consumer(@Payload String message,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) throws JsonProcessingException {
        AnswerDto answerDto = objectMapper.readValue(message, AnswerDto.class);
        Optional<MessageForKafkaEntity> messageForKafka
                = messageForKafkaRepository.findById(UUID.fromString(key));
        if (messageForKafka.isEmpty() || messageForKafka.get().getAccepted()) {
            throw new KafkaException("Problem with message");
        }
        documentService.updateFromKafkaMessage(answerDto);
        messageForKafkaRepository.updateAcceptedById(true, messageForKafka.get().getId());
    }
}
