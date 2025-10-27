package org.neiasalgados.domain.dto.response;

import org.neiasalgados.domain.entity.User;
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

    public UserResponseDTO(User user) {
        this.name = user.getName();
        this.surname = user.getSurname();
        this.cpf = maskCpf(user.getCpf());
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.isActive = user.isActive();
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
