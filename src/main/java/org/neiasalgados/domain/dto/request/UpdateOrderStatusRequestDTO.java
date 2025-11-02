package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class UpdateOrderStatusRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "O campo 'idOrder' não pode ser vazio")
    private Long idOrder;
    @NotBlank(message = "O campo 'orderStatus' não pode ser vazio")
    private String orderStatus;

    public UpdateOrderStatusRequestDTO() { }

    public UpdateOrderStatusRequestDTO(Long idOrder, String orderStatus) {
        this.idOrder = idOrder;
        this.orderStatus = orderStatus;
    }

    public Long getIdOrder() {
        return idOrder;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
