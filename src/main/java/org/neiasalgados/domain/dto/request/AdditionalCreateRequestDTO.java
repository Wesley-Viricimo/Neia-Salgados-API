package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class AdditionalCreateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotBlank(message = "O campo 'description' não pode ser nulo")
    private String description;
    @NotNull(message = "O campo 'price' não pode ser nulo")
    private Double price;

    public AdditionalCreateRequestDTO() {}

    public AdditionalCreateRequestDTO(String description, Double price) {
        this.description = description;
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }
}
