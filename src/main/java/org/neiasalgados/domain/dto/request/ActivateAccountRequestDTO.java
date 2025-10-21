package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class ActivateAccountRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O campo 'email' não pode ser vazio")
    private String email;

    @NotBlank(message = "O campo 'code' não pode ser vazio")
    private String code;

    public ActivateAccountRequestDTO() { }

    public ActivateAccountRequestDTO(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }
}
