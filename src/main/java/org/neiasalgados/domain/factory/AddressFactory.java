package org.neiasalgados.domain.factory;

import org.neiasalgados.domain.dto.request.AddressCreateRequestDTO;
import org.neiasalgados.domain.dto.request.AddressUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.ViaCepResponseDTO;
import org.neiasalgados.domain.entity.Address;
import org.neiasalgados.domain.entity.User;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.InvalidCepException;
import org.neiasalgados.repository.AddressRepository;
import org.neiasalgados.repository.UserRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
public class AddressFactory {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final RestTemplate restTemplate;

    public AddressFactory(AddressRepository addressRepository, UserRepository userRepository, AuthenticationFacade authenticationFacade) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
        this.restTemplate = new RestTemplate();
    }

    public Page<Address> findAddressesByUser(Pageable pageable) {
        User userAuthenticated = this.userRepository.findById(this.authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado"));

        return this.addressRepository.findByUser(userAuthenticated, pageable);
    }

    public ViaCepResponseDTO getAddressByCep(String cep) {
        String formattedCep = cep.replaceAll("\\D", "");

        if (formattedCep.length() != 8)
            throw new InvalidCepException("CEP deve conter 8 dígitos");

        try {
            String url = String.format("http://viacep.com.br/ws/%s/json/", formattedCep);
            ViaCepResponseDTO response = this.restTemplate.getForObject(url, ViaCepResponseDTO.class);

            if (response == null || response.getCep() == null)
                throw new InvalidCepException(String.format("CEP '%s' não encontrado", formattedCep));

            return response;
        } catch (RestClientException e) {
            throw new InvalidCepException("Erro ao consultar o CEP. Verifique se o CEP é válido");
        }
    }

    public Address createAddress(AddressCreateRequestDTO addressCreateRequestDTO) {
        User user = this.userRepository.findById(this.authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado"));

        return new Address(
                user,
                addressCreateRequestDTO.getCep(),
                addressCreateRequestDTO.getState(),
                addressCreateRequestDTO.getCity(),
                addressCreateRequestDTO.getDistrict(),
                addressCreateRequestDTO.getRoad(),
                addressCreateRequestDTO.getNumber(),
                addressCreateRequestDTO.getComplement()
        );
    }

    public Address updateAddress(AddressUpdateRequestDTO addressUpdateRequestDTO) {
        User userAuthenticated = this.userRepository.findById(this.authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado"));

        Address address = this.addressRepository.findById(addressUpdateRequestDTO.getIdAddress())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Endereço id '%s' não encontrado", addressUpdateRequestDTO.getIdAddress())));

        if (!userAuthenticated.getIdUser().equals(address.getUser().getIdUser()))
            throw new DataIntegrityViolationException("Endereço não pertence ao usuário autenticado");

        if (addressUpdateRequestDTO.getCep() != null)
            address.setCep(addressUpdateRequestDTO.getCep());

        if (addressUpdateRequestDTO.getState() != null)
            address.setState(addressUpdateRequestDTO.getState());

        if (addressUpdateRequestDTO.getCity() != null)
            address.setCity(addressUpdateRequestDTO.getCity());

        if (addressUpdateRequestDTO.getDistrict() != null)
            address.setDistrict(addressUpdateRequestDTO.getDistrict());

        if (addressUpdateRequestDTO.getRoad() != null)
            address.setRoad(addressUpdateRequestDTO.getRoad());

        if (addressUpdateRequestDTO.getNumber() != null)
            address.setNumber(addressUpdateRequestDTO.getNumber());

        if (addressUpdateRequestDTO.getComplement() != null)
            address.setComplement(addressUpdateRequestDTO.getComplement());

        address.setUpdatedAt(LocalDateTime.now());

        return address;
    }

    public Address deleteAddress(Long idAddress) {
        User userAuthenticated = this.userRepository.findById(this.authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado"));

        Address address = this.addressRepository.findById(idAddress)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Endereço id '%s' não encontrado", idAddress)));

        if (!userAuthenticated.getIdUser().equals(address.getUser().getIdUser()))
            throw new DataIntegrityViolationException("Endereço não pertence ao usuário autenticado");

        return address;
    }
}
