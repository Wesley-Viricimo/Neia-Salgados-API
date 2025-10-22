package org.neiasalgados.domain.dto.response;

import org.neiasalgados.domain.enums.UserRole;

public class UserResponseDTO {
    private String name;

    private String surname;

    private String cpf;

    private String phone;

    private String email;
    private UserRole role;

    private boolean isActive;

    public UserResponseDTO() {}

    public UserResponseDTO(String name, String surname, String cpf, String phone, String email, UserRole role, boolean isActive) {
        this.name = name;
        this.surname = surname;
        this.cpf = maskCpf(cpf);
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
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

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return isActive;
    }

    private String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return String.format("***.%s.***-%s",
                cpf.substring(3, 6),
                cpf.substring(9));
    }
}
