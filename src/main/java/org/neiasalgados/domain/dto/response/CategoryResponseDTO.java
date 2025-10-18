package org.neiasalgados.domain.dto.response;

import java.io.Serializable;

public class CategoryResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idCategory;

    private String description;

    public CategoryResponseDTO() { }

    public CategoryResponseDTO(Long idCategory, String description) {
        this.idCategory = idCategory;
        this.description = description;
    }

    public Long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(Long idCategory) {
        this.idCategory = idCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
