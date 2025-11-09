package org.neiasalgados.services;

import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.request.AddressCreateRequestDTO;
import org.neiasalgados.domain.dto.request.AddressUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.*;
import org.neiasalgados.domain.entity.Address;
import org.neiasalgados.domain.factory.AddressFactory;
import org.neiasalgados.repository.AddressRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressFactory addressFactory;

    public AddressService(AddressRepository addressRepository, AddressFactory addressFactory) {
        this.addressRepository = addressRepository;
        this.addressFactory = addressFactory;
    }

    public ResponseDataDTO<PageResponseDTO<AddressResponseDTO>> findAddressesByUser(Pageable pageable) {
        Page<Address> addresses = this.addressFactory.findAddressesByUser(pageable);
        Page<AddressResponseDTO> addressResponseDTO = addresses.map(AddressResponseDTO::new);
        PageResponseDTO<AddressResponseDTO> pageResponse = new PageResponseDTO<>(addressResponseDTO);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Endereços listados com sucesso"));
        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
    }

    public ResponseDataDTO<ViaCepResponseDTO> findAddressByCep(String cep) {
        ViaCepResponseDTO response = this.addressFactory.getAddressByCep(cep);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Endereço encontrado para o CEP informado"));
        return new ResponseDataDTO<>(response, messageResponse, HttpStatus.OK.value());
    }

    @Transactional
    public ResponseDataDTO<AddressResponseDTO> create(AddressCreateRequestDTO addressCreateRequestDTO) {
        Address address = this.addressRepository.save(this.addressFactory.createAddress(addressCreateRequestDTO));
        AddressResponseDTO addressResponseDTO = new AddressResponseDTO(address);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Endereço cadastrado com sucesso"));
        return new ResponseDataDTO<>(addressResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public ResponseDataDTO<AddressResponseDTO> update(AddressUpdateRequestDTO addressUpdateRequestDTO) {
        Address updatedAddress = this.addressRepository.save(this.addressFactory.updateAddress(addressUpdateRequestDTO));
        AddressResponseDTO addressResponseDTO = new AddressResponseDTO(updatedAddress);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Endereço atualizado com sucesso"));
        return new ResponseDataDTO<>(addressResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public void deleteAddress(Long idAddress) {
        Address address = this.addressFactory.deleteAddress(idAddress);
        this.addressRepository.delete(address);
    }
}
