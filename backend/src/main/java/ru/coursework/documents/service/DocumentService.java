package ru.coursework.documents.service;

import ru.coursework.documents.controller.dto.AnswerDto;
import ru.coursework.documents.controller.dto.DocumentDto;

import java.util.List;
import java.util.Set;

/**
 * Сервис по работе с документами
 */
public interface DocumentService {
    /**
     * Сохранить документ
     *
     * @param documentDto документ
     * @return сохраненный документ
     */
    DocumentDto save(DocumentDto documentDto);

    /**
     * Удалить документ
     *
     * @param ids идентификаторы документов
     */
    void deleteAll(Set<Long> ids);

    /**
     * Удалить документ по ид
     *
     * @param id идентификатор документа
     */
    void delete(Long id);

    /**
     * Обновить документ
     *
     * @param documentDto документ
     * @return обновленный документ
     */
    DocumentDto update(DocumentDto documentDto);

    /**
     * Получить все документы
     *
     * @return список документов
     */
    List<DocumentDto> findAll();

    /**
     * Получить документ по номеру
     *
     * @param id идентификатор
     * @return документ
     */
    DocumentDto get(Long id);

    /**
     * Отправить документ на подтверждение
     *
     * @param id идентификатор документа
     * @return документ
     */
    DocumentDto sendOnApprove(Long id);

    void updateFromKafkaMessage(AnswerDto answerDto);
}
