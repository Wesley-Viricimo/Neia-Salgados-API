package org.neiasalgados.domain.dto.response;

import org.neiasalgados.domain.entity.OrderItem;

import java.io.Serializable;

public class OrderItemResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String description;
    private Double price;
    private Integer quantity;
    private String comment;

    public OrderItemResponseDTO() {}

    public OrderItemResponseDTO(OrderItem orderItem) {
        this.description = orderItem.getDescription();
        this.price = orderItem.getPrice();
        this.quantity = orderItem.getQuantity();
        this.comment = orderItem.getComment();
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
