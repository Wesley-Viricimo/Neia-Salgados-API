package org.neiasalgados.services;

import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.dto.response.ViaCepResponseDTO;
import org.neiasalgados.exceptions.InvalidCepException;
import org.neiasalgados.repository.AddressRepository;
import org.neiasalgados.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;


    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
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
