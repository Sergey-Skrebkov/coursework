package ru.coursework.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Ответ из кафки
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    @NotNull
    private Long id;
    @NotNull
    private String status;
}
