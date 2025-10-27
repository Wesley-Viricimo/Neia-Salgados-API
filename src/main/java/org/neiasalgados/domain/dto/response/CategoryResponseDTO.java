package org.neiasalgados.domain.dto.response;

import org.neiasalgados.domain.entity.Category;

import java.io.Serializable;

public class CategoryResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long idCategory;

    private String description;

    public CategoryResponseDTO() { }

    public CategoryResponseDTO(Category category) {
        this.idCategory = category.getIdCategory();
        this.description = category.getDescription();
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
