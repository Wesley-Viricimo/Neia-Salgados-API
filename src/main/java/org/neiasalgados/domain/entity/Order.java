package org.neiasalgados.domain.entity;

import jakarta.persistence.*;
import org.neiasalgados.domain.enums.OrderStatus;
import org.neiasalgados.domain.enums.PaymentMethods;
import org.neiasalgados.domain.enums.TypeOfDelivery;
import org.neiasalgados.exceptions.DataIntegrityViolationException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "T_ORDER")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORDER", length = 16)
    private Long idOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ADDRESS")
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATUS", nullable = false)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD", nullable = false)
    private PaymentMethods paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE_OF_DELIVERY", nullable = false)
    private TypeOfDelivery typeOfDelivery;

    @Column(name = "TOTAL_ADDITIONAL")
    private Double totalAdditional;

    @Column(name = "TOTAL_PRICE", nullable = false)
    private Double totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderAdditional> additionals;

    @Column(name = "DELIVERY_DATE")
    private LocalDateTime deliveryDate;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    public Order() {}

    public Order(User user, Address address, PaymentMethods paymentMethod, TypeOfDelivery typeOfDelivery, Double totalAdditional, Double totalPrice, List<OrderItem> items, List<OrderAdditional> additionals) {
        this.validateOrder(user, address, typeOfDelivery, totalAdditional, totalPrice, items);
        this.user = user;
        this.address = address;
        this.orderStatus = OrderStatus.RECEBIDO;
        this.paymentMethod = paymentMethod;
        this.typeOfDelivery = typeOfDelivery;
        this.totalAdditional = totalAdditional;
        this.totalPrice = totalPrice;
        this.items = items;
        this.additionals = additionals;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Long idOrder) {
        this.idOrder = idOrder;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public List<OrderAdditional> getAdditionals() {
        return additionals;
    }

    public void setAdditionals(List<OrderAdditional> additionals) {
        this.additionals = additionals;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    private void validateOrder(User user, Address address, TypeOfDelivery typeOfDelivery, Double totalAdditional, Double totalPrice, List<OrderItem> items) {
        if (address != null) {
            if (!address.getUser().getIdUser().equals(user.getIdUser()))
                throw new DataIntegrityViolationException("Endereço não pertence ao usuário");
        }

        if (typeOfDelivery == TypeOfDelivery.ENTREGA && address == null)
            throw new DataIntegrityViolationException("Endereço deve ser fornecido para o tipo de entrega 'ENTREGA'");

        if (items == null || items.isEmpty())
            throw new DataIntegrityViolationException("Não é permitido realizar um pedido sem itens");

        if (totalPrice < 0)
            throw new DataIntegrityViolationException("O valor total do pedido não pode ser negativo");

        if (totalAdditional < 0)
            throw new DataIntegrityViolationException("O valor total dos adicionais não pode ser negativo");
    }
}