package org.neiasalgados.domain.dto;

import org.neiasalgados.domain.entity.OrderItem;
import java.util.List;

public class OrderItemsResultDTO {
    private List<OrderItem> items;
    private double totalPrice;

    public OrderItemsResultDTO(List<OrderItem> items, double totalPrice) {
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
