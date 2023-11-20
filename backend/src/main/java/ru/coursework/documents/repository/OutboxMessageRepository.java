package ru.coursework.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.documents.entity.OutboxMessageEntity;

import java.util.List;
import java.util.UUID;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessageEntity, UUID> {
    List<OutboxMessageEntity> findBySendFalse();
}