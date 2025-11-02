package org.neiasalgados.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.neiasalgados.domain.entity.Order;
import org.neiasalgados.domain.enums.OrderStatus;
import org.neiasalgados.domain.enums.PaymentMethods;
import org.neiasalgados.domain.enums.TypeOfDelivery;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idOrder;
    private UserResponseDTO user;
    private OrderAddressResponseDTO address;
    private OrderStatus orderStatus;
    private PaymentMethods paymentMethod;
    private TypeOfDelivery typeOfDelivery;
    private List<OrderItemResponseDTO> items;
    private List<OrderAdditionalResponseDTO> additionals;
    private Double totalAdditional;
    private Double totalPrice;
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private LocalDateTime deliveryDate;

    public OrderResponseDTO() { }

    public OrderResponseDTO(Order order) {
        this.idOrder = order.getIdOrder();
        this.user = new UserResponseDTO(order.getUser());
        this.address = order.getAddress() != null ? new OrderAddressResponseDTO(order.getAddress()) : null;
        this.orderStatus = order.getOrderStatus();
        this.paymentMethod = order.getPaymentMethod();
        this.typeOfDelivery = order.getTypeOfDelivery();
        this.items = order.getItems().stream().map(OrderItemResponseDTO::new).toList();
        this.additionals = order.getAdditionals().stream().map(OrderAdditionalResponseDTO::new).toList();
        this.totalAdditional = order.getTotalAdditional();
        this.totalPrice = order.getTotalPrice();
        this.deliveryDate = order.getDeliveryDate();
    }

    public Long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Long idOrder) {
        this.idOrder = idOrder;
    }

    public UserResponseDTO getUser() {
        return user;
    }

    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    public OrderAddressResponseDTO getAddress() {
        return address;
    }

    public void setAddress(OrderAddressResponseDTO address) {
        this.address = address;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public PaymentMethods getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethods paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public TypeOfDelivery getTypeOfDelivery() {
        return typeOfDelivery;
    }

    public void setTypeOfDelivery(TypeOfDelivery typeOfDelivery) {
        this.typeOfDelivery = typeOfDelivery;
    }

    public List<OrderItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponseDTO> items) {
        this.items = items;
    }

    public List<OrderAdditionalResponseDTO> getAdditionals() {
        return additionals;
    }

    public void setAdditionals(List<OrderAdditionalResponseDTO> additionals) {
        this.additionals = additionals;
    }

    public Double getTotalAdditional() {
        return totalAdditional;
    }

    public void setTotalAdditional(Double totalAdditional) {
        this.totalAdditional = totalAdditional;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
