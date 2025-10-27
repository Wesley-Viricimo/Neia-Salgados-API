package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class OrderAdditionalRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "O campo 'idAdditional' não pode ser vazio")
    private Long idAdditional;

    public OrderAdditionalRequestDTO() {}

    public OrderAdditionalRequestDTO(Long idAdditional) {
        this.idAdditional = idAdditional;
    }

    public Long getIdAdditional() {
        return idAdditional;
    }
}
