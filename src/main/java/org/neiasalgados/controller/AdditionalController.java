package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.request.AdditionalCreateRequestDTO;
import org.neiasalgados.domain.dto.request.AdditionalUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.AdditionalResponseDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.security.annotations.AllowRole;
import org.neiasalgados.services.AdditionalService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/neiasalgados/api/v1/additional")
public class AdditionalController {
    private final AdditionalService additionalService;

    public AdditionalController(AdditionalService additionalService) {
        this.additionalService = additionalService;
    }

    @GetMapping
    public ResponseEntity<ResponseDataDTO<PageResponseDTO<AdditionalResponseDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "description", required = false) String description
    ) {
        var dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "description"));
        return ResponseEntity.ok(additionalService.findAll(description, pageable));
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR, UserRole.COMERCIAL})
    @PostMapping
    public ResponseEntity<ResponseDataDTO<AdditionalResponseDTO>> create(@Valid @RequestBody AdditionalCreateRequestDTO additionalCreateRequestDTO) {
        ResponseDataDTO<AdditionalResponseDTO> response = additionalService.createAdditional(additionalCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR, UserRole.COMERCIAL})
    @PatchMapping
    public ResponseEntity<ResponseDataDTO<AdditionalResponseDTO>> update(@Valid @RequestBody AdditionalUpdateRequestDTO additionalUpdateRequestDTO) {
        ResponseDataDTO<AdditionalResponseDTO> response = additionalService.updateAdditional(additionalUpdateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR, UserRole.COMERCIAL})
    @DeleteMapping(value = "/{idAdditional}")
    public ResponseEntity<?> delelteAdditional(@PathVariable Long idAdditional) {
        this.additionalService.deleteAdditional(idAdditional);
        return ResponseEntity.noContent().build();
    }
}
