package org.neiasalgados.domain.dto.response;

import org.neiasalgados.domain.entity.Address;
import java.io.Serializable;

public class OrderAddressResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long idAddress;

    private String cep;

    private String state;

    private String city;

    private String district;

    private String road;

    private String number;

    private String complement;

    public OrderAddressResponseDTO() {}

    public OrderAddressResponseDTO(Address address) {
        this.idAddress = address.getIdAddress();
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

    public void setIdAddress(Long idAddress) {
        this.idAddress = idAddress;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }
}
