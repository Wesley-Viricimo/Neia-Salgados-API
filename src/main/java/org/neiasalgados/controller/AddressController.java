package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.request.AddressRequestDTO;
import org.neiasalgados.domain.dto.response.AddressResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.dto.response.ViaCepResponseDTO;
import org.neiasalgados.services.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/neiasalgados/api/v1/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<ResponseDataDTO<AddressResponseDTO>> createAddress(@Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        ResponseDataDTO<AddressResponseDTO> response = addressService.createAddress(addressRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/find-address-by-cep/{cep}")
    public ResponseEntity<ResponseDataDTO<ViaCepResponseDTO>> findAddressByCep(@PathVariable(value = "cep") String cep) {
        return ResponseEntity.ok(addressService.findAddressByCep(cep));
    }
}
