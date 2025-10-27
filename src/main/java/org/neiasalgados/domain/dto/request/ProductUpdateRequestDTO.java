package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class ProductUpdateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "O campo 'idProduct' não pode ser vazio")
    private Long idProduct;
    private String title;
    private String description;
    private Double price;
    private Long idCategory;

    public ProductUpdateRequestDTO() { }

    public ProductUpdateRequestDTO(Long idProduct, String title, String description, Double price, Long idCategory) {
        this.idProduct = idProduct;
        this.title = title;
        this.description = description;
        this.price = price;
        this.idCategory = idCategory;
    }

    public Long getIdProduct() {
        return idProduct;
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
