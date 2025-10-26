package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class AdditionalUpdateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "O campo 'idAdditional' não pode ser nulo")
    private Long idAdditional;
    private String description;
    private Double price;

    public AdditionalUpdateRequestDTO() {}

    public AdditionalUpdateRequestDTO(Long idAdditional, String description, Double price) {
        this.idAdditional = idAdditional;
        this.description = description;
        this.price = price;
    }

    public Long getIdAdditional() {
        return idAdditional;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }
}
