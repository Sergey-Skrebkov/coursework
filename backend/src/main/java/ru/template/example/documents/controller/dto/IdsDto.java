package ru.template.example.documents.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class IdsDto {
    @NotEmpty
    private Set<Long> ids;

}
