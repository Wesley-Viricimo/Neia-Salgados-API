package org.neiasalgados.domain.dto.response;

import org.neiasalgados.domain.entity.Additional;

import java.io.Serializable;

public class AdditionalResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long idAdditional;
    private String description;
    private Double price;

    public AdditionalResponseDTO() { }

    public AdditionalResponseDTO(Additional additional) {
        this.idAdditional = additional.getIdAdditional();
        this.description = additional.getDescription();
        this.price = additional.getPrice();
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
