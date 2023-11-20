package ru.coursework.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.documents.entity.DocumentEntity;
import ru.coursework.documents.entity.StatusEntity;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {
    @Transactional
    @Modifying
    @Query("update DocumentEntity d set d.status = ?1 where d.id = ?2")
    void updateStatusById(@Nullable StatusEntity status, Long id);

    @Override
    Optional<DocumentEntity> findById(Long id);

    @Override
    void deleteById(Long id);
}
