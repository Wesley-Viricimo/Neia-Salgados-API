package org.neiasalgados.domain.dto.request;

import java.io.Serializable;

public class OrderAddressRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long idAddress;

    public OrderAddressRequestDTO() { }

    public OrderAddressRequestDTO(Long idAddress) {
        this.idAddress = idAddress;
    }

    public Long getIdAddress() {
        return idAddress;
    }
}
