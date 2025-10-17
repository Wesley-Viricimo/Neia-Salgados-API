package org.neiasalgados.domain.vo;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class CategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O campo 'description' não pode ser vazio")
    private String description;

    public CategoryVO() { }

    public CategoryVO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
