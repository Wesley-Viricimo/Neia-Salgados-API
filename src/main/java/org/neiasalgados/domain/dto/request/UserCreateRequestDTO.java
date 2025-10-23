package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;

public class UserCreateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O campo 'name' não pode ser vazio")
    private String name;

    @NotBlank(message = "O campo 'surname' não pode ser vazio")
    private String surname;

    @CPF
    @NotBlank(message = "O campo 'cpf' não pode ser vazio")
    private String cpf;

    @NotBlank(message = "O campo 'phone' não pode ser vazio")
    private String phone;

    private String role;

    @Email
    @NotBlank(message = "O campo 'email' não pode ser vazio")
    private String email;

    @NotBlank(message = "O campo 'password' não pode ser vazio")
    private String password;

    public UserCreateRequestDTO() {}

    public UserCreateRequestDTO(String name, String surname, String cpf, String phone, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.cpf = cpf;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public UserCreateRequestDTO(String name, String surname, String cpf, String phone, String role, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.cpf = cpf;
        this.phone = phone;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCpf() {
        return cpf;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
