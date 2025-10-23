package org.neiasalgados.domain.dto.response;

import jakarta.validation.constraints.NotBlank;

public class AddressResponseDTO {
    private Long idAddress;
    private UserResponseDTO user;

    private String cep;

    private String state;

    private String city;

    private String district;

    private String road;

    private String number;

    private String complement;

    public AddressResponseDTO() { }

    public AddressResponseDTO(Long idAddress, UserResponseDTO user, String cep, String state, String city, String district, String road, String number, String complement) {
        this.idAddress = idAddress;
        this.user = user;
        this.cep = cep;
        this.state = state;
        this.city = city;
        this.district = district;
        this.road = road;
        this.number = number;
        this.complement = complement;
    }

    public Long getIdAddress() {
        return idAddress;
    }

    public UserResponseDTO getUser() {
        return user;
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
