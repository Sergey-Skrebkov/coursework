package ru.template.example.documents.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.entity.DocumentEntity;
import ru.template.example.documents.entity.StatusEntity;
import ru.template.example.documents.repository.DocumentRepository;
import ru.template.example.documents.repository.StatusRepository;
import ru.template.example.kafka.KafkaSender;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Lazy
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final StatusRepository statusRepository;
    private final KafkaSender kafkaSender;
    private final MapperFacade mapperFacade = new DefaultMapperFactory
            .Builder()
            .build()
            .getMapperFacade();

    public DocumentDto save(DocumentDto documentDto) {
        DocumentEntity document = mapperFacade.map(documentDto, DocumentEntity.class);
        Optional<StatusEntity> status = statusRepository.findByCode("NEW");
        document.setStatus(status.get());
        document.setDate(Instant.now());
        documentRepository.save(document);
        return mapperFacade.map(document, DocumentDto.class);
    }


    public DocumentDto update(DocumentDto documentDto) {
        kafkaSender.sendMessage(documentDto, "documents");
        Optional<DocumentEntity> documentEntityOptional = documentRepository.findById(documentDto.getId());
        Optional<StatusEntity> status = statusRepository.findByCode("IN_PROCESS");
        documentRepository.updateStatusById(status.get(), documentDto.getId());
        return documentDto;
    }

    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    public void deleteAll(Set<Long> ids) {
        ids.forEach(this::delete);
    }

    public List<DocumentDto> findAll() {
        List<DocumentEntity> documents
                = documentRepository.findAll();
        return mapperFacade.mapAsList(documents, DocumentDto.class);
    }

    public DocumentDto get(Long id) {
        Optional<DocumentEntity> document = documentRepository.findById(id);
        return mapperFacade.map(document.get(), DocumentDto.class);
    }
}
