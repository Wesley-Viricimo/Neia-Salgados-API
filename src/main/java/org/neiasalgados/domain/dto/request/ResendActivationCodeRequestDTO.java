package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class ResendActivationCodeRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O campo 'email' não pode ser vazio")
    private String email;

    public ResendActivationCodeRequestDTO() { }

    public ResendActivationCodeRequestDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
