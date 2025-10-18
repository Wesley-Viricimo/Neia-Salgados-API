package org.neiasalgados.domain.dto.response;

public class UserResponseDTO {
    private String name;

    private String surname;

    private String cpf;

    private String phone;

    private String email;

    public UserResponseDTO() {}

    public UserResponseDTO(String name, String surname, String cpf, String phone, String email) {
        this.name = name;
        this.surname = surname;
        this.cpf = cpf;
        this.phone = phone;
        this.email = email;
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
}
