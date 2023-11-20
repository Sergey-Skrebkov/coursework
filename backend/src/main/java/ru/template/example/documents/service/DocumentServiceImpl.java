package ru.template.example.documents.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.entity.DocumentEntity;
import ru.template.example.documents.entity.MessageForKafkaEntity;
import ru.template.example.documents.entity.StatusEntity;
import ru.template.example.documents.exception.DocumentApprovedException;
import ru.template.example.documents.exception.NoDocumentException;
import ru.template.example.documents.exception.NotSuchStatus;
import ru.template.example.documents.repository.DocumentRepository;
import ru.template.example.documents.repository.MessageForKafkaRepository;
import ru.template.example.documents.repository.StatusRepository;

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
        Optional<StatusEntity> status = statusRepository.findByCode("NEW");
        document.setStatus(status.orElseThrow(() -> new NotSuchStatus("Status not found")));
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
        Optional<StatusEntity> status = statusRepository.findByCode(documentDto.getStatus().getCode());
        documentRepository.updateStatusById(status
                        .orElseThrow(() -> new NotSuchStatus("Status not found")),
                documentDto.getId());
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
        Optional<StatusEntity> status = statusRepository.findByCode("IN_PROCESS");
        addToTableForKafkaSender(documentDto);
        documentRepository.updateStatusById(status
                        .orElseThrow(() -> new NotSuchStatus("Status not found")),
                documentDto.getId());
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
    private void checkDocument(Optional document) {
        if (document.isEmpty()) {
            throw new NoDocumentException("The document is not in the database");
        }
    }
}
