package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.request.AddressCreateRequestDTO;
import org.neiasalgados.domain.dto.request.AddressUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.AddressResponseDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.dto.response.ViaCepResponseDTO;
import org.neiasalgados.services.AddressService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<ResponseDataDTO<AddressResponseDTO>> createAddress(@Valid @RequestBody AddressCreateRequestDTO addressCreateRequestDTO) {
        ResponseDataDTO<AddressResponseDTO> response = addressService.create(addressCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/update")
    public ResponseEntity<ResponseDataDTO<AddressResponseDTO>> updateAddress(@Valid @RequestBody AddressUpdateRequestDTO addressUpdateRequestDTO) {
        ResponseDataDTO<AddressResponseDTO> response = addressService.update(addressUpdateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    ResponseEntity<ResponseDataDTO<PageResponseDTO<AddressResponseDTO>>> findAddressesByUser(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "city"));
        return ResponseEntity.ok(addressService.findAddressesByUser(pageable));
    }

    @GetMapping("/find-address-by-cep/{cep}")
    public ResponseEntity<ResponseDataDTO<ViaCepResponseDTO>> findAddressByCep(@PathVariable(value = "cep") String cep) {
        return ResponseEntity.ok(addressService.findAddressByCep(cep));
    }
}
