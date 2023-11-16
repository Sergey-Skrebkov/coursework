package ru.template.example.documents.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.template.example.documents.controller.dto.AnswerDto;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.controller.dto.Status;
import ru.template.example.documents.entity.MessageForKafkaAnswerEntity;
import ru.template.example.documents.entity.MessageForKafkaEntity;
import ru.template.example.documents.repository.MessageForKafkaRepository;
import ru.template.example.documents.service.DocumentService;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class KafkaAnswerConsumer {
    private final ObjectMapper objectMapper;
    private final DocumentService documentService;
    private final MessageForKafkaRepository messageForKafkaRepository;

    @KafkaListener(topics = "answer", groupId = "group_id")
    public void consumer(@Payload String message, @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) throws JsonProcessingException {
        AnswerDto answerDto = objectMapper.readValue(message, AnswerDto.class);
        Optional<MessageForKafkaEntity> messageForKafka
                = messageForKafkaRepository.findById(UUID.fromString(key));
        if (messageForKafka.isEmpty() || messageForKafka.get().getAccepted()) {
            throw new KafkaException("Problem with message");
        }
        updateDocument(answerDto);
        messageForKafkaRepository.updateAcceptedById(true, messageForKafka.get().getId());
    }

    private void updateDocument(AnswerDto answerDto) {
        DocumentDto documentDto = documentService.get(answerDto.getId());
        documentDto.setStatus(getStatusForDocument(answerDto.getStatus()));
        documentService.update(documentDto);
    }

    private void updateMessageForKafka(MessageForKafkaEntity messageForKafka) {
        messageForKafka.setAccepted(true);
    }

    private Status getStatusForDocument(String statusFromMessage) {
        if (statusFromMessage.equals("ACCEPTED")) {
            return new Status("ACCEPTED", "Принят");
        } else {
            return new Status("REJECTED", "Отклонен");
        }
    }
}
