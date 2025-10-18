package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AuthRequestDTO {

    @NotBlank(message = "O campo 'email' não pode ser vazio")
    private String email;

    @NotBlank(message = "O campo 'password' não pode ser vazio")
    private String password;

    public AuthRequestDTO() { }

    public AuthRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
