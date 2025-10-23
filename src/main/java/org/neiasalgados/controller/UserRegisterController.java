package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.request.ActivateAccountRequestDTO;
import org.neiasalgados.domain.dto.request.ResendActivationCodeRequestDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.dto.response.UserResponseDTO;
import org.neiasalgados.domain.dto.request.UserCreateRequestDTO;
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
    public ResponseEntity<ResponseDataDTO<UserResponseDTO>> create(@Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        ResponseDataDTO<UserResponseDTO> response = userRegisterService.createUser(userCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/activate-account")
    public ResponseEntity<ResponseDataDTO<UserResponseDTO>> activateAccount(@Valid @RequestBody ActivateAccountRequestDTO activateAccountRequestDTO) {
        ResponseDataDTO<UserResponseDTO> response = userRegisterService.activateAccount(activateAccountRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/resend-activation-code")
    public ResponseEntity<ResponseDataDTO<UserResponseDTO>> resendActivationCode(@Valid @RequestBody ResendActivationCodeRequestDTO resendActivationCodeRequestDTO) {
        ResponseDataDTO<UserResponseDTO> response = userRegisterService.resendActivationCode(resendActivationCodeRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
