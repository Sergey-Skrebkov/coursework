package ru.coursework.documents.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.coursework.documents.controller.dto.AnswerDto;
import ru.coursework.documents.exception.DocumentApprovedException;
import ru.coursework.documents.exception.NoDocumentException;
import ru.coursework.documents.controller.dto.DocumentDto;
import ru.coursework.documents.entity.DocumentEntity;
import ru.coursework.documents.entity.MessageForKafkaEntity;
import ru.coursework.documents.entity.StatusEntity;
import ru.coursework.documents.exception.NotSuchStatus;
import ru.coursework.documents.repository.DocumentRepository;
import ru.coursework.documents.repository.MessageForKafkaRepository;
import ru.coursework.documents.repository.StatusRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Lazy
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final StatusRepository statusRepository;
    private final MessageForKafkaRepository messageForKafkaRepository;
    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade = new DefaultMapperFactory
            .Builder()
            .build()
            .getMapperFacade();

    /**
     * Сохранить документ
     *
     * @param documentDto документ
     * @return документ
     */
    @Transactional
    public DocumentDto save(DocumentDto documentDto) {
        DocumentEntity document = mapperFacade.map(documentDto, DocumentEntity.class);
        StatusEntity status = getStatusForDocumentByName("NEW");
        document.setStatus(status);
        document.setDate(Instant.now());
        documentRepository.save(document);
        return mapperFacade.map(document, DocumentDto.class);
    }

    /**
     * Обновить документ
     *
     * @param documentDto документ
     * @return документ
     */
    @Transactional
    public DocumentDto update(DocumentDto documentDto) {
        Optional<DocumentEntity> documentEntityOptional = documentRepository.findById(documentDto.getId());
        checkDocument(documentEntityOptional);
        StatusEntity status = getStatusForDocumentByName(documentDto.getStatus().getCode());
        documentRepository.updateStatusById(status, documentDto.getId());
        return documentDto;
    }

    /**
     * Отправить документ на подтверждение
     *
     * @param documentDto документ
     * @return документ
     */
    @Transactional
    public DocumentDto sendOnApprove(DocumentDto documentDto) {
        Optional<DocumentEntity> documentEntityOptional = documentRepository.findById(documentDto.getId());
        if (!documentEntityOptional
                .orElseThrow(() -> new NoDocumentException("The document is not in the database"))
                .getStatus()
                .getCode()
                .equals("NEW")) {
            throw new DocumentApprovedException("The document has already been sent");
        }
        StatusEntity status = getStatusForDocumentByName("IN_PROCESS");
        addToTableForKafkaSender(documentDto);
        documentRepository.updateStatusById(status, documentDto.getId());
        return documentDto;
    }

    /**
     * Удаление одного документа по id
     *
     * @param id идентификатор документа
     */
    @Transactional
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    /**
     * Удалить несколько документов
     *
     * @param ids идентификаторы документов
     */
    @Transactional
    public void deleteAll(Set<Long> ids) {
        ids.forEach(this::delete);
    }

    /**
     * Получить список документов
     *
     * @return список документов
     */
    public List<DocumentDto> findAll() {
        List<DocumentEntity> documents
                = documentRepository.findAll();
        return mapperFacade.mapAsList(documents, DocumentDto.class);
    }

    /**
     * Получить документ по id
     *
     * @param id идентификатор документа
     * @return документ
     */
    public DocumentDto get(Long id) {
        Optional<DocumentEntity> document = documentRepository.findById(id);
        return mapperFacade.map(document.orElseThrow(() -> new NoDocumentException("The document is not in the database")), DocumentDto.class);
    }

    @Transactional
    public void updateFromKafkaMessage(AnswerDto answerDto) {
        StatusEntity status = getStatusForDocumentByName(answerDto.getStatus());
        Optional<DocumentEntity> document = documentRepository.findById(answerDto.getId());
        document.orElseThrow(() -> new NoDocumentException("The document is not in the database"))
                .setStatus(status);
        document.ifPresent(documentRepository::save);

    }

    /**
     * Найти статус по его названию
     *
     * @param statusName - имя статуса
     * @return статус
     */
    private StatusEntity getStatusForDocumentByName(String statusName) {
        Optional<StatusEntity> status = statusRepository.findByCode(statusName);
        return status.orElseThrow(() -> new NotSuchStatus("Status not found"));
    }

    /**
     * Добавить сообщения с документом в табличку для отправки сообщений
     *
     * @param documentDto документ
     */
    private void addToTableForKafkaSender(DocumentDto documentDto) {
        MessageForKafkaEntity message = new MessageForKafkaEntity();
        try {
            String json = objectMapper.writeValueAsString(documentDto);
            message.setMessage(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        message.setId(UUID.randomUUID());
        message.setSend(false);
        message.setCreateDate(Instant.now());
        messageForKafkaRepository.save(message);
    }

    /**
     * Проверка на наличие документа
     *
     * @param document документ полученный при запросе к бд
     */
    private void checkDocument(Optional<DocumentEntity> document) {
        if (document.isEmpty()) {
            throw new NoDocumentException("The document is not in the database");
        }
    }
}
