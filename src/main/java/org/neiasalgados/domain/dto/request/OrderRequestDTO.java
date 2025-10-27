package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class OrderRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private OrderAddressRequestDTO address;
    @NotBlank(message = "O campo 'paymentMethod' não pode ser vazio")
    private String paymentMethod;
    @NotBlank(message = "O campo 'typeOfDelivery' não pode ser vazio")
    private String typeOfDelivery;
    @NotNull(message = "O campo 'orderItems' não pode ser vazio")
    private List<OrderItemRequestDTO> orderItems;
    @NotNull(message = "O campo 'orderAdditionals' não pode ser vazio")
    private List<OrderAdditionalRequestDTO> orderAdditionals;

    public OrderRequestDTO() { }

    public OrderRequestDTO(OrderAddressRequestDTO address, String paymentMethod, String typeOfDelivery, List<OrderItemRequestDTO> orderItems, List<OrderAdditionalRequestDTO> orderAdditionals) {
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.typeOfDelivery = typeOfDelivery;
        this.orderItems = orderItems;
        this.orderAdditionals = orderAdditionals;
    }

    public OrderAddressRequestDTO getAddress() {
        return address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getTypeOfDelivery() {
        return typeOfDelivery;
    }

    public List<OrderItemRequestDTO> getOrderItems() {
        return orderItems;
    }

    public List<OrderAdditionalRequestDTO> getOrderAdditionals() {
        return orderAdditionals;
    }
}
