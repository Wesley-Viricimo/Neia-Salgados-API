package org.neiasalgados.services;

import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.MessageResponseDTO;
import org.neiasalgados.domain.dto.ResponseDataDTO;
import org.neiasalgados.domain.dto.UserDTO;
import org.neiasalgados.domain.entity.User;
import org.neiasalgados.domain.entity.UserActivationCode;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.domain.vo.UserVO;
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
    public ResponseDataDTO<UserDTO> createUser(UserVO userVO) {
        List<User> existingUsers = userRepository.findByEmailOrPhoneOrCpf(
                userVO.getEmail(),
                userVO.getPhone(),
                userVO.getCpf()
        );

        if (userVO.getPhone().length() > 11)
            throw new DataIntegrityViolationException("Telefone deve conter no máximo 11 dígitos (DDD + Número)");

        if (!existingUsers.isEmpty()) {
            List<String> duplicateFields = new ArrayList<>();
            existingUsers.forEach(user -> {
                if (user.getEmail().equals(userVO.getEmail())) {
                    duplicateFields.add(String.format("Email '%s' já cadastrado no sistema", userVO.getEmail()));
                }
                if (user.getPhone().equals(userVO.getPhone())) {
                    duplicateFields.add(String.format("Telefone '%s' já cadastrado no sistema", userVO.getPhone()));
                }
                if (user.getCpf().equals(userVO.getCpf())) {
                    duplicateFields.add(String.format("CPF '%s' já cadastrado no sistema", userVO.getCpf()));
                }
            });

            throw new DuplicateFieldsException(duplicateFields);
        }

        var user = new User(
                userVO.getName(),
                userVO.getSurname(),
                userVO.getCpf(),
                userVO.getPhone(),
                userVO.getEmail(),
                this.passwordEncoder.encode(userVO.getPassword()),
                UserRole.CLIENTE
        );

        user = userRepository.save(user);
        var userActivationCode = new UserActivationCode(user, this.generateActivationCode());
        this.userActivationCodeRepository.save(userActivationCode);

        //TODO: PRECISO CRIAR A ROTINA DE ENVIAR EMAIL E COLOCAR NESTE TRECHO

        var userDTO = new UserDTO(user.getName(), user.getSurname(), user.getCpf(), user.getPhone(), user.getEmail());
        var messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Usuário cadastrado com sucesso"));
        return new ResponseDataDTO<>(userDTO, messageResponse, HttpStatus.CREATED.value());
    }

    private String generateActivationCode() {
        return Long.toString(Double.doubleToLongBits(Math.random()), 36)
                .substring(0, 5)
                .toUpperCase();
    }
}
