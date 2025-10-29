package org.neiasalgados.domain.dto;

import org.neiasalgados.domain.entity.OrderAdditional;

import java.util.List;

public class OrderAdditionalsResultDTO {
    private List<OrderAdditional> additionals;
    private double totalPrice;

    public OrderAdditionalsResultDTO(List<OrderAdditional> additionals, double totalPrice) {
        this.additionals = additionals;
        this.totalPrice = totalPrice;
    }

    public List<OrderAdditional> getAdditionals() {
        return additionals;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
