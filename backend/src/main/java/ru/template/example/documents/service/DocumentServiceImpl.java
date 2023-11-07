package ru.template.example.documents.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.controller.dto.Status;
import ru.template.example.documents.entity.DocumentEntity;
import ru.template.example.documents.entity.StatusEntity;
import ru.template.example.documents.repository.DocumentRepository;
import ru.template.example.documents.repository.StatusRepository;
import ru.template.example.documents.store.DocumentStore;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Lazy
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final StatusRepository statusRepository;
    private final MapperFacade mapperFacade = new DefaultMapperFactory
            .Builder()
            .build()
            .getMapperFacade();

    public DocumentDto save(DocumentDto documentDto) {
        if (documentDto.getId() == null) {
            //Докинуть ошибку сюда
        }
        Optional<DocumentEntity> documentEntityOptional = documentRepository.findById(documentDto.getId());
        if(documentEntityOptional.isEmpty()){
            //throw
        }
        documentEntityOptional.get().setDate(Instant.now());
        if (documentDto.getStatus() == null) {
            documentDto.setStatus(Status.of("NEW", "Новый"));
        }
        DocumentStore.getInstance().getDocumentDtos().add(documentDto);
        return documentDto;
    }


    public DocumentDto update(DocumentDto documentDto) {
        Optional<DocumentEntity> documentEntityOptional = documentRepository.findById(documentDto.getId());
        Optional<StatusEntity> status = statusRepository.findByCode("IN_PROCESS");
        documentRepository.updateStatusById(status.get(), documentDto.getId());
        return documentDto;
    }

    public void delete(Long id) {
        List<DocumentDto> documentDtos = DocumentStore.getInstance().getDocumentDtos();
        List<DocumentDto> newList = documentDtos.stream()
                .filter(d -> !d.getId().equals(id)).collect(Collectors.toList());
        documentDtos.clear();
        documentDtos.addAll(newList);
    }

    public void deleteAll(Set<Long> ids) {
        List<DocumentDto> documentDtos = DocumentStore.getInstance().getDocumentDtos();
        List<DocumentDto> newList = documentDtos.stream()
                .filter(d -> !ids.contains(d.getId())).collect(Collectors.toList());
        documentDtos.clear();
        documentDtos.addAll(newList);
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
