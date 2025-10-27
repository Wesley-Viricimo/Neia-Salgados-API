package org.neiasalgados.domain.dto.response;

import org.neiasalgados.domain.entity.Product;

import java.io.Serializable;

public class ProductResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idProduct;
    private CategoryResponseDTO category;
    private String title;
    private String description;
    private String urlImage;
    private Double price;

    public ProductResponseDTO() { }

    public ProductResponseDTO(Product product) {
        this.idProduct = product.getIdProduct();
        this.category = new CategoryResponseDTO(product.getCategory().getIdCategory(), product.getCategory().getDescription());
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.urlImage = product.getUrlImage();
        this.price = product.getPrice();
    }

    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public CategoryResponseDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryResponseDTO category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
