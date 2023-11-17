package ru.template.example.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.template.example.documents.entity.MessageForKafkaEntity;

import java.util.List;
import java.util.UUID;

public interface MessageForKafkaRepository extends JpaRepository<MessageForKafkaEntity, UUID> {
    List<MessageForKafkaEntity> findBySendFalse();

    @Transactional
    @Modifying
    @Query("update MessageForKafkaEntity d set d.accepted = ?1 where d.id = ?2")
    void updateAcceptedById(boolean accepted, UUID id);
}