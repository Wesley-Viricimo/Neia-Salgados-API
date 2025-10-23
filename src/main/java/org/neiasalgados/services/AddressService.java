package org.neiasalgados.services;

import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.request.AddressRequestDTO;
import org.neiasalgados.domain.dto.response.*;
import org.neiasalgados.domain.entity.Address;
import org.neiasalgados.exceptions.InvalidCepException;
import org.neiasalgados.repository.AddressRepository;
import org.neiasalgados.repository.UserRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;
    private final RestTemplate restTemplate;


    public AddressService(AddressRepository addressRepository, UserRepository userRepository, AuthenticationFacade authenticationFacade) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    public ResponseDataDTO<AddressResponseDTO> createAddress(AddressRequestDTO addressRequestDTO) {
        var user = userRepository.findById(this.authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Address address = this.addressRepository.save(new Address(user, addressRequestDTO.getCep(), addressRequestDTO.getState(), addressRequestDTO.getCity(), addressRequestDTO.getDistrict(), addressRequestDTO.getRoad(), addressRequestDTO.getNumber(), addressRequestDTO.getComplement()));

        AddressResponseDTO addressResponseDTO = new AddressResponseDTO(
                address.getIdAddress(),
                new UserResponseDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail(), user.getRole(), user.isActive()),
                address.getCep(),
                address.getState(),
                address.getCity(),
                address.getDistrict(),
                address.getRoad(),
                address.getNumber(),
                address.getComplement()
        );

        var messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Endereço cadastrado com sucesso"));
        return new ResponseDataDTO<>(addressResponseDTO, messageResponse, HttpStatus.CREATED.value());

    }

    public ResponseDataDTO<ViaCepResponseDTO> findAddressByCep(String cep) {
        String formattedCep = cep.replaceAll("\\D", "");

        if (formattedCep.length() != 8)
            throw new InvalidCepException("CEP deve conter 8 dígitos");

        try {
            String url = String.format("http://viacep.com.br/ws/%s/json/", formattedCep);
            ViaCepResponseDTO response = restTemplate.getForObject(url, ViaCepResponseDTO.class);

            if (response == null || response.getCep() == null)
                throw new InvalidCepException(String.format("CEP '%s' não encontrado", formattedCep));

            var messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Endereço encontrado para o CEP informado"));
            return new ResponseDataDTO<>(response, messageResponse, HttpStatus.OK.value());
        } catch (RestClientException e) {
            throw new InvalidCepException("Erro ao consultar o CEP. Verifique se o CEP é válido");
        }
    }
}
