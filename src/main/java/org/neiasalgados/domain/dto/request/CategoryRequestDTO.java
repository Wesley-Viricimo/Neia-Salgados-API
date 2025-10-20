package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class CategoryRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O campo 'description' não pode ser vazio")
    private String description;

    public CategoryRequestDTO() { }

    public CategoryRequestDTO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
