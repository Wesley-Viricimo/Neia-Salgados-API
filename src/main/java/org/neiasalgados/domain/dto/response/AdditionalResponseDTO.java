package org.neiasalgados.domain.dto.response;

import java.io.Serializable;

public class AdditionalResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long idAdditional;
    private String description;
    private Double price;

    public AdditionalResponseDTO() { }

    public AdditionalResponseDTO(Long idAdditional, String description, Double price) {
        this.idAdditional = idAdditional;
        this.description = description;
        this.price = price;
    }

    public Long getIdAdditional() {
        return idAdditional;
    }

    public void setIdAdditional(Long idAdditional) {
        this.idAdditional = idAdditional;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
