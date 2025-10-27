package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class OrderItemRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "O campo 'idProduct' não pode ser vazio")
    private Long idProduct;
    @NotNull(message = "O campo 'quantity' não pode ser vazio")
    private Integer quantity;

    public OrderItemRequestDTO() {}

    public OrderItemRequestDTO(Long idProduct, Integer quantity) {
        this.idProduct = idProduct;
        this.quantity = quantity;
    }

    public Long getIdProduct() {
        return idProduct;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
