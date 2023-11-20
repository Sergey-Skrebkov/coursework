package ru.coursework.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.coursework.documents.entity.StatusEntity;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<StatusEntity, Long> {
    Optional<StatusEntity> findByCode(@NonNull String code);
}
