package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class ProductCreateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O campo 'title' não pode ser vazio")
    private String title;
    @NotBlank(message = "O campo 'description' não pode ser vazio")
    private String description;
    @NotNull(message = "O campo 'price' não pode ser vazio")
    private Double price;
    @NotNull(message = "O campo 'idCategory' não pode ser vazio")
    private Long idCategory;

    public ProductCreateRequestDTO() { }

    public ProductCreateRequestDTO(String title, String description, Double price, Long idCategory) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.idCategory = idCategory;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Long getIdCategory() {
        return idCategory;
    }
}
