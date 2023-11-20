package ru.coursework.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    /**
     * Номер
     */
    private Long id;
    /**
     * Вид документа
     */
    @NotBlank
    private String type;
    /**
     * Организация
     */
    @NotBlank
    private String organization;
    /**
     * Описание
     */
    private String description;
    /**
     * Пациент
     */
    @NotBlank
    @Pattern(regexp = "^([а-яА-Я]+[-\\s]?[а-яА-Я]+)\\s*^([а-яА-Я]+[-\\s]?[а-яА-Я]+)?\\s*^([а-яА-Я]+[-\\s]?[а-яА-Я]+)?$")
    private String patient;
    /**
     * Дата документа
     */
    private Date date;
    /**
     * Статус
     */
    private Status status;
}
