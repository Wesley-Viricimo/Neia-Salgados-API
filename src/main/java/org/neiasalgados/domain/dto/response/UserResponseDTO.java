package org.neiasalgados.domain.dto.response;

public class UserResponseDTO {
    private String name;

    private String surname;

    private String cpf;

    private String phone;

    private String email;

    private boolean isActive;

    public UserResponseDTO() {}

    public UserResponseDTO(String name, String surname, String cpf, String phone, String email, boolean isActive) {
        this.name = name;
        this.surname = surname;
        this.cpf = maskCpf(cpf);
        this.phone = phone;
        this.email = email;
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
