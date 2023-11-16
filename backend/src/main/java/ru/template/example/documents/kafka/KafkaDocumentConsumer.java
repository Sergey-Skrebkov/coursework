package ru.template.example.documents.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.template.example.documents.controller.dto.AnswerDto;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.entity.MessageForKafkaAnswerEntity;
import ru.template.example.documents.repository.MessageForKafkaAnswerRepository;
import ru.template.example.documents.repository.MessageForKafkaRepository;

import java.util.UUID;

@Component
@AllArgsConstructor
public class KafkaDocumentConsumer {
    private final ObjectMapper objectMapper;
    private final MessageForKafkaRepository messageForKafkaRepository;
    private final MessageForKafkaAnswerRepository messageForKafkaAnswerRepository;

    @KafkaListener(topics = "documents", groupId = "group_id")
    public void consumer(@Payload String message, @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) throws JsonProcessingException {
        DocumentDto documentDto = objectMapper.readValue(message, DocumentDto.class);
        AnswerDto answerDto = new AnswerDto(documentDto.getId(), "ACCEPTED");
        MessageForKafkaAnswerEntity messageForKafkaAnswer
                = prepareMessageEntity(UUID.fromString(key), answerDto);
        messageForKafkaAnswerRepository.save(messageForKafkaAnswer);
    }

    private MessageForKafkaAnswerEntity prepareMessageEntity(UUID key, AnswerDto answerDto){
        MessageForKafkaAnswerEntity messageForKafkaAnswer = new MessageForKafkaAnswerEntity();
        messageForKafkaAnswer.setId(key);
        messageForKafkaAnswer.setSend(false);
        try {
            String json = objectMapper.writeValueAsString(answerDto);
            messageForKafkaAnswer.setMessage(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return messageForKafkaAnswer;
    }
}
