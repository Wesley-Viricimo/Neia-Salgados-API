package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class AddressCreateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "O campo 'cep' não pode ser vazio")
    private String cep;

    @NotBlank(message = "O campo 'state' não pode ser vazio")
    private String state;

    @NotBlank(message = "O campo 'city' não pode ser vazio")
    private String city;

    @NotBlank(message = "O campo 'district' não pode ser vazio")
    private String district;

    @NotBlank(message = "O campo 'road' não pode ser vazio")
    private String road;

    @NotBlank(message = "O campo 'number' não pode ser vazio")
    private String number;

    private String complement;

    public AddressCreateRequestDTO() { }

    public AddressCreateRequestDTO(String cep, String state, String city, String district, String road, String number, String complement) {
        this.cep = cep;
        this.state = state;
        this.city = city;
        this.district = district;
        this.road = road;
        this.number = number;
        this.complement = complement;
    }

    public String getCep() {
        return cep;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getRoad() {
        return road;
    }

    public String getNumber() {
        return number;
    }

    public String getComplement() {
        return complement;
    }
}
