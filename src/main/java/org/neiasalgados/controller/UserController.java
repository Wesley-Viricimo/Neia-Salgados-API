package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.request.ChangeUserActivitieRequestDTO;
import org.neiasalgados.domain.dto.request.UpdateUserRoleRequestDTO;
import org.neiasalgados.domain.dto.request.UserCreateRequestDTO;
import org.neiasalgados.domain.dto.request.UserUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.dto.response.UserResponseDTO;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.security.annotations.AllowRole;
import org.neiasalgados.services.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/neiasalgados/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR, UserRole.COMERCIAL})
    @GetMapping
    public ResponseEntity<ResponseDataDTO<PageResponseDTO<UserResponseDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "name", required = false) String name
    ) {
        var dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "name"));
        return ResponseEntity.ok(userService.findAll(name, pageable));
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR})
    @PostMapping("/create-admin")
    public ResponseEntity<ResponseDataDTO<UserResponseDTO>> createAdmin(@Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        ResponseDataDTO<UserResponseDTO> response = userService.createAdmin(userCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR})
    @PutMapping("/update-user-role")
    public ResponseEntity<ResponseDataDTO<UserResponseDTO>> updateUserRole(@Valid @RequestBody UpdateUserRoleRequestDTO updateUserRoleRequestDTO) {
        ResponseDataDTO<UserResponseDTO> response = userService.updateUserRole(updateUserRoleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR})
    @PutMapping("/change-user-activitie")
    public ResponseEntity<ResponseDataDTO<UserResponseDTO>> updateUserRole(@Valid @RequestBody ChangeUserActivitieRequestDTO changeUserActivitieRequestDTO) {
        ResponseDataDTO<UserResponseDTO> response = userService.changeUserActivitie(changeUserActivitieRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/update-user")
    public ResponseEntity<ResponseDataDTO<UserResponseDTO>> updateUser(@Valid @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        ResponseDataDTO<UserResponseDTO> response = userService.updateUser(userUpdateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
