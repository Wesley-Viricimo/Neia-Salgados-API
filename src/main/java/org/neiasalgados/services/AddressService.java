package org.neiasalgados.services;

import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.request.AddressCreateRequestDTO;
import org.neiasalgados.domain.dto.request.AddressUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.*;
import org.neiasalgados.domain.entity.Address;
import org.neiasalgados.domain.entity.User;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.InvalidCepException;
import org.neiasalgados.repository.AddressRepository;
import org.neiasalgados.repository.UserRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public ResponseDataDTO<AddressResponseDTO> create(AddressCreateRequestDTO addressCreateRequestDTO) {
        User user = userRepository.findById(this.authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado"));

        Address address = this.addressRepository.save(new Address(user, addressCreateRequestDTO.getCep(), addressCreateRequestDTO.getState(), addressCreateRequestDTO.getCity(), addressCreateRequestDTO.getDistrict(), addressCreateRequestDTO.getRoad(), addressCreateRequestDTO.getNumber(), addressCreateRequestDTO.getComplement()));

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

        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Endereço cadastrado com sucesso"));
        return new ResponseDataDTO<>(addressResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public ResponseDataDTO<AddressResponseDTO> update(AddressUpdateRequestDTO addressUpdateRequestDTO) {
        User user = userRepository.findById(this.authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado"));

        Address address = this.addressRepository.findById(addressUpdateRequestDTO.getIdAddress())
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Endereço id '%s' não encontrado", addressUpdateRequestDTO.getIdAddress())));

        if (!user.getIdUser().equals(address.getUser().getIdUser()))
            throw new DataIntegrityViolationException("Endereço não pertence ao usuário autenticado");

        if (addressUpdateRequestDTO.getCep() != null && !addressUpdateRequestDTO.getCep().isEmpty())
            address.setCep(addressUpdateRequestDTO.getCep());

        if (addressUpdateRequestDTO.getState() != null && !addressUpdateRequestDTO.getState().isEmpty())
            address.setState(addressUpdateRequestDTO.getState());

        if (addressUpdateRequestDTO.getCity() != null && !addressUpdateRequestDTO.getCity().isEmpty())
            address.setCity(addressUpdateRequestDTO.getCity());

        if (addressUpdateRequestDTO.getDistrict() != null && !addressUpdateRequestDTO.getDistrict().isEmpty())
            address.setDistrict(addressUpdateRequestDTO.getDistrict());

        if (addressUpdateRequestDTO.getRoad() != null && !addressUpdateRequestDTO.getRoad().isEmpty())
            address.setRoad(addressUpdateRequestDTO.getRoad());

        if (addressUpdateRequestDTO.getNumber() != null && !addressUpdateRequestDTO.getNumber().isEmpty())
            address.setNumber(addressUpdateRequestDTO.getNumber());

        if (addressUpdateRequestDTO.getComplement() != null && !addressUpdateRequestDTO.getComplement().isEmpty())
            address.setComplement(addressUpdateRequestDTO.getComplement());

        address.setUpdatedAt(java.time.LocalDateTime.now());
        Address updatedAddress = this.addressRepository.save(address);

        AddressResponseDTO addressResponseDTO = new AddressResponseDTO(
                updatedAddress.getIdAddress(),
                new UserResponseDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail(), user.getRole(), user.isActive()),
                updatedAddress.getCep(),
                updatedAddress.getState(),
                updatedAddress.getCity(),
                updatedAddress.getDistrict(),
                updatedAddress.getRoad(),
                updatedAddress.getNumber(),
                updatedAddress.getComplement()
        );

        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Endereço atualizado com sucesso"));
        return new ResponseDataDTO<>(addressResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    public ResponseDataDTO<PageResponseDTO<AddressResponseDTO>> findAddressesByUser(Pageable pageable) {
        User user = userRepository.findById(this.authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado"));

        Page<Address> addresses = this.addressRepository.findByUser(user, pageable);
        Page<AddressResponseDTO> addressResponseDTO = addresses.map(address -> new AddressResponseDTO(
                address.getIdAddress(),
                new UserResponseDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail(), user.getRole(), user.isActive()),
                address.getCep(),
                address.getState(),
                address.getCity(),
                address.getDistrict(),
                address.getRoad(),
                address.getNumber(),
                address.getComplement()
        ));

        PageResponseDTO<AddressResponseDTO> pageResponse = new PageResponseDTO<>(addressResponseDTO);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Endereços listados com sucesso"));

        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
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

            MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Endereço encontrado para o CEP informado"));
            return new ResponseDataDTO<>(response, messageResponse, HttpStatus.OK.value());
        } catch (RestClientException e) {
            throw new InvalidCepException("Erro ao consultar o CEP. Verifique se o CEP é válido");
        }
    }
}
