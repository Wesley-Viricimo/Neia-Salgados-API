package org.neiasalgados.domain.dto.response;

import org.neiasalgados.domain.entity.Additional;
import org.neiasalgados.domain.entity.OrderAdditional;

import java.io.Serializable;

public class OrderAdditionalResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String description;
    private Double price;

    public OrderAdditionalResponseDTO() { }

    public OrderAdditionalResponseDTO(OrderAdditional orderAdditional) {
        this.description = orderAdditional.getDescription();
        this.price = orderAdditional.getPrice();
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
