package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class AddressUpdateRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "O campo 'idAddress' não pode ser vazio")
    private Long idAddress;

    private String cep;

    private String state;

    private String city;

    private String district;

    private String road;

    private String number;

    private String complement;

    public AddressUpdateRequestDTO() { }

    public AddressUpdateRequestDTO(Long idAddress, String cep, String state, String city, String district, String road, String number, String complement) {
        this.idAddress = idAddress;
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
