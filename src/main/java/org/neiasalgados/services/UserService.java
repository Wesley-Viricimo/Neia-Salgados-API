package org.neiasalgados.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.ActionAuditingDTO;
import org.neiasalgados.domain.dto.request.ChangeUserActivitieRequestDTO;
import org.neiasalgados.domain.dto.request.UpdateUserRoleRequestDTO;
import org.neiasalgados.domain.dto.request.UserCreateRequestDTO;
import org.neiasalgados.domain.dto.request.UserUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.dto.response.UserResponseDTO;
import org.neiasalgados.domain.entity.User;
import org.neiasalgados.domain.entity.UserActivationCode;
import org.neiasalgados.domain.enums.ChangeType;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.DuplicateFieldsException;
import org.neiasalgados.repository.UserActivationCodeRepository;
import org.neiasalgados.repository.UserRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.neiasalgados.utils.ActivationCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserActivationCodeRepository userActivationCodeRepository;
    private final AuditingService auditingService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationFacade authenticationFacade;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository, UserActivationCodeRepository userActivationCodeRepository, AuditingService auditingService, AuthenticationFacade authenticationFacade, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.userActivationCodeRepository = userActivationCodeRepository;
        this.auditingService = auditingService;
        this.authenticationFacade = authenticationFacade;
        this.objectMapper = objectMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public ResponseDataDTO<PageResponseDTO<UserResponseDTO>> findAll(String name, Pageable pageable) {
        Page<User> userPage = Optional.ofNullable(name)
                .filter(nm -> !nm.isEmpty())
                .map(nm -> userRepository.findByNameContainingIgnoreCase(nm, pageable))
                .orElseGet(() -> userRepository.findAll(pageable));

        Page<UserResponseDTO> userResponseDTOPage = userPage.map(user -> new UserResponseDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail(), user.getRole(), user.isActive()));
        PageResponseDTO<UserResponseDTO> pageResponse = new PageResponseDTO<>(userResponseDTOPage);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Usuários listados com sucesso"));

        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
    }

    @Transactional
    public ResponseDataDTO<UserResponseDTO> createAdmin(UserCreateRequestDTO userCreateRequestDTO) {
        if (userCreateRequestDTO.getRole() == null)
            throw new DataIntegrityViolationException("O campo 'role' não pode ser vazio");

        User userAdmin = userRepository.findById(authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new DataIntegrityViolationException("Usuário autenticado não encontrado"));

        UserRole userRole = validateAndGetUserRole(userCreateRequestDTO.getRole());

        if (userRole == UserRole.DESENVOLVEDOR && userAdmin.getRole() != UserRole.DESENVOLVEDOR)
            throw new DataIntegrityViolationException("Apenas usuários com a role 'DESENVOLVEDOR' podem criar outros usuários com essa role");

        List<User> existingUsers = userRepository.findByEmailOrPhoneOrCpf(
                userCreateRequestDTO.getEmail(),
                userCreateRequestDTO.getPhone(),
                userCreateRequestDTO.getCpf()
        );

        if (userCreateRequestDTO.getPhone().length() > 11)
            throw new DataIntegrityViolationException("Telefone deve conter no máximo 11 dígitos (DDD + Número)");

        if (!existingUsers.isEmpty()) {
            List<String> duplicateFields = new ArrayList<>();
            existingUsers.forEach(user -> {
                if (user.getEmail().equals(userCreateRequestDTO.getEmail())) {
                    duplicateFields.add(String.format("Email '%s' já cadastrado no sistema", userCreateRequestDTO.getEmail()));
                }
                if (user.getPhone().equals(userCreateRequestDTO.getPhone())) {
                    duplicateFields.add(String.format("Telefone '%s' já cadastrado no sistema", userCreateRequestDTO.getPhone()));
                }
                if (user.getCpf().equals(userCreateRequestDTO.getCpf())) {
                    duplicateFields.add(String.format("CPF '%s' já cadastrado no sistema", userCreateRequestDTO.getCpf()));
                }
            });

            throw new DuplicateFieldsException(duplicateFields);
        }

        User user = userRepository.save(new User(
                userCreateRequestDTO.getName(),
                userCreateRequestDTO.getSurname(),
                userCreateRequestDTO.getCpf(),
                userCreateRequestDTO.getPhone(),
                userCreateRequestDTO.getEmail(),
                this.passwordEncoder.encode(userCreateRequestDTO.getPassword()),
                userRole,
                true
        ));

        UserActivationCode activationCode = new UserActivationCode(user, ActivationCode.generateActivationCode(), true);
        this.userActivationCodeRepository.save(activationCode);

        try {
            String userJson = objectMapper.writeValueAsString(user);
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    userAdmin.getIdUser(),
                    "CADASTRO DE USUARIO ADMINISTRADOR",
                    "USUARIO",
                    user.getIdUser(),
                    null,
                    userJson,
                    ChangeType.CREATE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        UserResponseDTO userDTO = new UserResponseDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail(), user.getRole(), user.isActive());
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Usuário cadastrado com sucesso"));
        return new ResponseDataDTO<>(userDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public ResponseDataDTO<UserResponseDTO> updateUserRole(UpdateUserRoleRequestDTO updateUserRoleRequestDTO) {
        User user = userRepository.findById(updateUserRoleRequestDTO.getUserId())
                .orElseThrow(() -> new DataIntegrityViolationException("Usuário não encontrado"));

        if (!user.isActive())
            throw new DataIntegrityViolationException("Não é possível alterar a role de um usuário inativo");

        User userAdmin = userRepository.findById(authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new DataIntegrityViolationException("Usuário autenticado não encontrado"));

        if (userAdmin.getIdUser().equals(updateUserRoleRequestDTO.getUserId()))
            throw new DataIntegrityViolationException("Usuário não pode alterar sua própria role");

        UserRole userRole = validateAndGetUserRole(updateUserRoleRequestDTO.getNewRole());

        if (user.getRole() == UserRole.ADMINISTRADOR && userAdmin.getRole() == UserRole.ADMINISTRADOR && userRole != UserRole.ADMINISTRADOR)
            throw new DataIntegrityViolationException("Não é permitido que usuários com a role 'ADMINISTRADOR' altere privilégios de outros usuários 'ADMINISTRADOR'");

        if (userRole == UserRole.DESENVOLVEDOR && userAdmin.getRole() != UserRole.DESENVOLVEDOR)
            throw new DataIntegrityViolationException("Apenas usuários com a role 'DESENVOLVEDOR' podem atribuir outros usuários com essa role");

        if (userRole == UserRole.ADMINISTRADOR && userAdmin.getRole() != UserRole.DESENVOLVEDOR)
            throw new DataIntegrityViolationException("Apenas usuários com a role 'DESENVOLVEDOR' podem atribuir a role 'ADMINISTRADOR'");

        try {
            String beforeChangeJson = objectMapper.writeValueAsString(user);

            user.setRole(userRole);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            String afterChangeJson = objectMapper.writeValueAsString(user);
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    userAdmin.getIdUser(),
                    "ALTERAÇÃO DE ROLE DE USUÁRIO",
                    "USUARIO",
                    user.getIdUser(),
                    beforeChangeJson,
                    afterChangeJson,
                    ChangeType.UPDATE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        UserResponseDTO userDTO = new UserResponseDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail(), user.getRole(), user.isActive());
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Role do usuário atualizada com sucesso"));
        return new ResponseDataDTO<>(userDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public ResponseDataDTO<UserResponseDTO> changeUserActivitie(ChangeUserActivitieRequestDTO changeUserActivitieRequestDTO) {
        User user = userRepository.findById(changeUserActivitieRequestDTO.getUserId())
                .orElseThrow(() -> new DataIntegrityViolationException("Usuário não encontrado"));

        User userAdmin = userRepository.findById(authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new DataIntegrityViolationException("Usuário autenticado não encontrado"));

        if (userAdmin.getIdUser().equals(changeUserActivitieRequestDTO.getUserId()))
            throw new DataIntegrityViolationException("Usuário não pode alterar sua própria atividade");

        if ((user.getRole() == UserRole.ADMINISTRADOR || user.getRole() == UserRole.DESENVOLVEDOR) && userAdmin.getRole() != UserRole.DESENVOLVEDOR)
            throw new DataIntegrityViolationException("Somente usuários com a role 'DESENVOLVEDOR' podem alterar a atividade de usuários 'ADMINISTRADOR' ou 'DESENVOLVEDOR'");

        try {
            String beforeChangeJson = objectMapper.writeValueAsString(user);

            user.setActive(changeUserActivitieRequestDTO.isActive());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            String afterChangeJson = objectMapper.writeValueAsString(user);
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    userAdmin.getIdUser(),
                    "ALTERAÇÃO DE ATIVIDADE DE USUÁRIO",
                    "USUARIO",
                    user.getIdUser(),
                    beforeChangeJson,
                    afterChangeJson,
                    ChangeType.UPDATE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        UserResponseDTO userDTO = new UserResponseDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail(), user.getRole(), user.isActive());
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Atividade do usuário atualizada com sucesso"));
        return new ResponseDataDTO<>(userDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public ResponseDataDTO<UserResponseDTO> updateUser(UserUpdateRequestDTO userUpdateRequestDTO) {
        User user = userRepository.findById(authenticationFacade.getAuthenticatedUserId())
                .orElseThrow(() -> new DataIntegrityViolationException("Usuário autenticado não encontrado"));

        List<String> duplicateFields = new ArrayList<>();

        if (userUpdateRequestDTO.getEmail() != null && !userUpdateRequestDTO.getEmail().isEmpty())
            userRepository.findByEmailAndIdUserNot(userUpdateRequestDTO.getEmail(), user.getIdUser())
                    .ifPresent(u -> duplicateFields.add(String.format("Email '%s' já cadastrado no sistema", userUpdateRequestDTO.getEmail())));

        if (userUpdateRequestDTO.getPhone() != null && !userUpdateRequestDTO.getPhone().isEmpty())
            userRepository.findByPhoneAndIdUserNot(userUpdateRequestDTO.getPhone(), user.getIdUser())
                    .ifPresent(u -> duplicateFields.add(String.format("Telefone '%s' já cadastrado no sistema", userUpdateRequestDTO.getPhone())));

        if (userUpdateRequestDTO.getCpf() != null && !userUpdateRequestDTO.getCpf().isEmpty())
            userRepository.findByCpfAndIdUserNot(userUpdateRequestDTO.getCpf(), user.getIdUser())
                    .ifPresent(u -> duplicateFields.add(String.format("CPF '%s' já cadastrado no sistema", userUpdateRequestDTO.getCpf())));

        if (!duplicateFields.isEmpty())
            throw new DuplicateFieldsException(duplicateFields);

        if (userUpdateRequestDTO.getName() != null && !userUpdateRequestDTO.getName().isEmpty())
            user.setName(userUpdateRequestDTO.getName());

        if (userUpdateRequestDTO.getSurname() != null && !userUpdateRequestDTO.getSurname().isEmpty())
            user.setSurname(userUpdateRequestDTO.getSurname());

        if (userUpdateRequestDTO.getCpf() != null && !userUpdateRequestDTO.getCpf().isEmpty())
            user.setCpf(userUpdateRequestDTO.getCpf());

        if (userUpdateRequestDTO.getPhone() != null && !userUpdateRequestDTO.getPhone().isEmpty())
            user.setPhone(userUpdateRequestDTO.getPhone());

        if (userUpdateRequestDTO.getEmail() != null && !userUpdateRequestDTO.getEmail().isEmpty())
            user.setEmail(userUpdateRequestDTO.getEmail());

        if (userUpdateRequestDTO.getPassword() != null && !userUpdateRequestDTO.getPassword().isEmpty())
            user.setPassword(passwordEncoder.encode(userUpdateRequestDTO.getPassword()));

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        UserResponseDTO userDTO = new UserResponseDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail(), user.getRole(), user.isActive());
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Dados do usuário atualizados com sucesso"));
        return new ResponseDataDTO<>(userDTO, messageResponse, HttpStatus.OK.value());
    }

    private UserRole validateAndGetUserRole(String roleStr) {
        String normalizedRole = roleStr.toUpperCase();
        boolean isValidRole = Arrays.stream(UserRole.values())
                .anyMatch(role -> role.name().equals(normalizedRole));

        if (!isValidRole) {
            throw new DataIntegrityViolationException("Role inválida. As roles permitidas são: " +
                    Arrays.toString(UserRole.values()));
        }

        return UserRole.valueOf(normalizedRole);
    }
}