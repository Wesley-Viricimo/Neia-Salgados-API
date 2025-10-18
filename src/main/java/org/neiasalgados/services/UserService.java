package org.neiasalgados.services;

import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.dto.response.UserResponseDTO;
import org.neiasalgados.domain.entity.User;
import org.neiasalgados.domain.entity.UserActivationCode;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.domain.dto.request.UserRequestDTO;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.DuplicateFieldsException;
import org.neiasalgados.repository.UserActivationCodeRepository;
import org.neiasalgados.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserActivationCodeRepository userActivationCodeRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserActivationCodeRepository userActivationCodeRepository) {
        this.userRepository = userRepository;
        this.userActivationCodeRepository = userActivationCodeRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public ResponseDataDTO<UserResponseDTO> createUser(UserRequestDTO userRequestDTO) {
        List<User> existingUsers = userRepository.findByEmailOrPhoneOrCpf(
                userRequestDTO.getEmail(),
                userRequestDTO.getPhone(),
                userRequestDTO.getCpf()
        );

        if (userRequestDTO.getPhone().length() > 11)
            throw new DataIntegrityViolationException("Telefone deve conter no máximo 11 dígitos (DDD + Número)");

        if (!existingUsers.isEmpty()) {
            List<String> duplicateFields = new ArrayList<>();
            existingUsers.forEach(user -> {
                if (user.getEmail().equals(userRequestDTO.getEmail())) {
                    duplicateFields.add(String.format("Email '%s' já cadastrado no sistema", userRequestDTO.getEmail()));
                }
                if (user.getPhone().equals(userRequestDTO.getPhone())) {
                    duplicateFields.add(String.format("Telefone '%s' já cadastrado no sistema", userRequestDTO.getPhone()));
                }
                if (user.getCpf().equals(userRequestDTO.getCpf())) {
                    duplicateFields.add(String.format("CPF '%s' já cadastrado no sistema", userRequestDTO.getCpf()));
                }
            });

            throw new DuplicateFieldsException(duplicateFields);
        }

        var user = userRepository.save(new User(
                userRequestDTO.getName(),
                userRequestDTO.getSurname(),
                userRequestDTO.getCpf(),
                userRequestDTO.getPhone(),
                userRequestDTO.getEmail(),
                this.passwordEncoder.encode(userRequestDTO.getPassword()),
                UserRole.CLIENTE
        ));

        var userActivationCode = new UserActivationCode(user, this.generateActivationCode());
        this.userActivationCodeRepository.save(userActivationCode);

        //TODO: PRECISO CRIAR A ROTINA DE ENVIAR EMAIL E COLOCAR NESTE TRECHO

        var userDTO = new UserResponseDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail());
        var messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Usuário cadastrado com sucesso"));
        return new ResponseDataDTO<>(userDTO, messageResponse, HttpStatus.CREATED.value());
    }

    private String generateActivationCode() {
        return Long.toString(Double.doubleToLongBits(Math.random()), 36)
                .substring(0, 5)
                .toUpperCase();
    }
}
