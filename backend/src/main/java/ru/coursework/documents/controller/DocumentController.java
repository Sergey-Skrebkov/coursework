package ru.coursework.documents.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.coursework.documents.controller.dto.DocumentDto;
import ru.coursework.documents.controller.dto.IdDto;
import ru.coursework.documents.controller.dto.IdsDto;
import ru.coursework.documents.service.DocumentService;

import javax.validation.Valid;
import java.util.List;

/**
 * Рест контроллер для обработки запросов
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService service;

    /**
     * Пост запрос для записи документа
     *
     * @param dto документ из тела запроса
     * @return сохранённый документ
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto save(@Valid @RequestBody DocumentDto dto) {
        return service.save(dto);
    }

    /**
     * Запрос для получению списка документов
     *
     * @return список документов
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DocumentDto> get() {
        return service.findAll();
    }

    /**
     * Запрос для отправки документа на обработку
     *
     * @param id идентификатор документа
     * @return документ отправленный на подтверждение
     */
    @PostMapping(
            path = "send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto send(@Valid @RequestBody IdDto id) {
        return service.sendOnApprove(id.getId());
    }

    /**
     * Запрос на удаление документа по идентификатору документа полученому из параметра
     *
     * @param id идентификатор документа
     */
    @DeleteMapping(path = "/{id}")
    public void delete(@Valid @PathVariable Long id) {
        service.delete(id);
    }

    /**
     * Удаление нескольких документов по набору идентификаторов
     *
     * @param idsDto список id
     */
    @DeleteMapping
    public void deleteAll(@Valid @RequestBody IdsDto idsDto) {
        service.deleteAll(idsDto.getIds());
    }

}
