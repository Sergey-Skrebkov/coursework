package ru.template.example.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.template.example.documents.entity.MessageForKafkaAnswerEntity;

import java.util.List;
import java.util.UUID;

public interface MessageForKafkaAnswerRepository extends JpaRepository<MessageForKafkaAnswerEntity, UUID> {
    List<MessageForKafkaAnswerEntity> findBySendFalse();
}