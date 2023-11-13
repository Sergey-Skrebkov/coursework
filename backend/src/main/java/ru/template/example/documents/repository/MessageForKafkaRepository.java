package ru.template.example.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.template.example.documents.entity.MessageForKafkaEntity;

import java.util.List;

public interface MessageForKafkaRepository extends JpaRepository<MessageForKafkaEntity, Long> {
    List<MessageForKafkaEntity> findBySendFalse();

}