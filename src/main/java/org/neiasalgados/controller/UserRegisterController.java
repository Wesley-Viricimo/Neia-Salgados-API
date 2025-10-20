package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.request.ActivateAccountDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.dto.response.UserResponseDTO;
import org.neiasalgados.domain.dto.request.UserRequestDTO;
import org.neiasalgados.services.UserRegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neiasalgados/api/v1/user-register")
public class UserRegisterController {

    private final UserRegisterService userRegisterService;

    public UserRegisterController(UserRegisterService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }

    @PostMapping
    public ResponseEntity<ResponseDataDTO<UserResponseDTO>> create(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        ResponseDataDTO<UserResponseDTO> response = userRegisterService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/activate-account")
    public ResponseEntity<ResponseDataDTO<UserResponseDTO>> activateAccount(@Valid @RequestBody ActivateAccountDTO activateAccountDTO) {
        ResponseDataDTO<UserResponseDTO> response = userRegisterService.activateAccount(activateAccountDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
