package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class CategoryUpdateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "O campo 'idCategory' não pode ser vazio")
    private Long idCategory;
    @NotBlank(message = "O campo 'description' não pode ser vazio")
    private String description;

    public CategoryUpdateRequestDTO() { }

    public CategoryUpdateRequestDTO(Long idCategory, String description) {
        this.idCategory = idCategory;
        this.description = description;
    }

    public Long getIdCategory() {
        return idCategory;
    }

    public String getDescription() {
        return description;
    }
}
