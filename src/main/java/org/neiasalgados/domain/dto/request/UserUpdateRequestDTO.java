package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.br.CPF;
import java.io.Serializable;

public class UserUpdateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private String surname;

    @CPF
    private String cpf;

    private String phone;

    @Email
    private String email;

    private String password;

    public UserUpdateRequestDTO() {}

    public UserUpdateRequestDTO(String name, String surname, String cpf, String phone, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.cpf = cpf;
        this.phone = phone;
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

