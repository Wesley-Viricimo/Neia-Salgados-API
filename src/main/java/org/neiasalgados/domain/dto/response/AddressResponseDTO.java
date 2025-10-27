package org.neiasalgados.domain.dto.response;

import jakarta.validation.constraints.NotBlank;
import org.neiasalgados.domain.entity.Address;

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

    public AddressResponseDTO(Address address) {
        this.idAddress = address.getIdAddress();
        this.user = new UserResponseDTO(address.getUser());
        this.cep = address.getCep();
        this.state = address.getState();
        this.city = address.getCity();
        this.district = address.getDistrict();
        this.road = address.getRoad();
        this.number = address.getNumber();
        this.complement = address.getComplement();
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
