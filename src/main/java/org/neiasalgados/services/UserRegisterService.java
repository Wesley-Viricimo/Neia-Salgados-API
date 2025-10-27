package org.neiasalgados.services;

import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.request.ActivateAccountRequestDTO;
import org.neiasalgados.domain.dto.request.ResendActivationCodeRequestDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.dto.response.UserResponseDTO;
import org.neiasalgados.domain.entity.User;
import org.neiasalgados.domain.entity.UserActivationCode;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.domain.dto.request.UserCreateRequestDTO;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.DuplicateFieldsException;
import org.neiasalgados.repository.UserActivationCodeRepository;
import org.neiasalgados.repository.UserRepository;
import org.neiasalgados.utils.ActivationCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserRegisterService {
    private final UserRepository userRepository;
    private final UserActivationCodeRepository userActivationCodeRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserRegisterService(UserRepository userRepository, UserActivationCodeRepository userActivationCodeRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.userActivationCodeRepository = userActivationCodeRepository;
        this.emailService = emailService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public ResponseDataDTO<UserResponseDTO> createUser(UserCreateRequestDTO userCreateRequestDTO) {
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
                UserRole.CLIENTE
        ));

        UserActivationCode userActivationCode = new UserActivationCode(user, ActivationCode.generateActivationCode());
        this.userActivationCodeRepository.save(userActivationCode);

        this.sendActivationEmail(user.getEmail(), userActivationCode.getCode(), user.getSurname());

        UserResponseDTO userDTO = new UserResponseDTO(user);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Usuário cadastrado com sucesso"));
        return new ResponseDataDTO<>(userDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public ResponseDataDTO<UserResponseDTO> activateAccount(ActivateAccountRequestDTO activateAccountRequestDTO) {
        User user = this.userRepository.findByEmail(activateAccountRequestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não está cadastrado no sistema"));

        if (user.isActive())
            throw new DataIntegrityViolationException("Usuário já está ativo no sistema");

        UserActivationCode activationCode = this.userActivationCodeRepository.findByUserAndCode(user, activateAccountRequestDTO.getCode().toUpperCase())
                .orElseThrow(() -> new DataIntegrityViolationException("Código de ativação inválido"));

        LocalDateTime currentTime = LocalDateTime.now();

        user.setActive(true);
        user.setUpdatedAt(currentTime);
        activationCode.setConfirmed(true);
        activationCode.setUpdatedAt(currentTime);
        this.userActivationCodeRepository.save(activationCode);
        this.userRepository.save(user);

        UserResponseDTO userDTO = new UserResponseDTO(user);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Usuário ativo com sucesso"));
        return new ResponseDataDTO<>(userDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public ResponseDataDTO<UserResponseDTO> resendActivationCode(ResendActivationCodeRequestDTO resendActivationCodeRequestDTO) {
        User user = this.userRepository.findByEmail(resendActivationCodeRequestDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não está cadastrado no sistema"));

        if (user.isActive())
            throw new DataIntegrityViolationException(String.format("Usuário email '%s' já está ativo no sistema, não é necessário reenviar o email para ativação da conta", resendActivationCodeRequestDTO.getEmail()));

        UserActivationCode activationCode = this.userActivationCodeRepository.findByUser(user)
                .orElseThrow(() ->  new DataIntegrityViolationException(String.format("Código de ativação não encontrado para o email '%s'", resendActivationCodeRequestDTO.getEmail())));

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime lastUpdate = activationCode.getUpdatedAt();

        if (lastUpdate.plusMinutes(3).isAfter(currentTime))
            throw new DataIntegrityViolationException("Código de ativação já foi reenviado recentemente. Por favor, aguarde 3 minutos antes de solicitar um novo código.");

        activationCode.setUpdatedAt(currentTime);
        this.userActivationCodeRepository.save(activationCode);

        this.sendActivationEmail(user.getEmail(), activationCode.getCode(), user.getSurname());

        UserResponseDTO userDTO = new UserResponseDTO(user);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Código de ativação reenviado com sucesso"));
        return new ResponseDataDTO<>(userDTO, messageResponse, HttpStatus.CREATED.value());
    }

    private void sendActivationEmail(String toEmail, String activationCode, String surname) {
        String htmlMessage = "<html>" +
                "<head>" +
                "<style>" +
                "body {" +
                "    font-family: Arial, sans-serif;" +
                "    background-color: #f4f4f9;" +
                "    color: #333;" +
                "    margin: 0;" +
                "    padding: 0;" +
                "}" +
                ".email-container {" +
                "    max-width: 600px;" +
                "    margin: 20px auto;" +
                "    background-color: #ffffff;" +
                "    border-radius: 8px;" +
                "    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);" +
                "    overflow: hidden;" +
                "    border: 1px solid #ddd;" +
                "}" +
                ".header {" +
                "    background-color: #ff6f61;" +
                "    color: white;" +
                "    padding: 20px;" +
                "    text-align: center;" +
                "    font-size: 24px;" +
                "    font-weight: bold;" +
                "}" +
                ".content {" +
                "    padding: 20px;" +
                "    text-align: left;" +
                "    line-height: 1.6;" +
                "}" +
                ".content p {" +
                "    margin: 15px 0;" +
                "}" +
                ".activation-code {" +
                "    display: inline-block;" +
                "    padding: 10px 15px;" +
                "    margin: 20px 0;" +
                "    font-size: 18px;" +
                "    font-weight: bold;" +
                "    color: white;" +
                "    background-color: #ff6f61;" +
                "    border-radius: 4px;" +
                "    text-align: center;" +
                "}" +
                ".footer {" +
                "    background-color: #f4f4f9;" +
                "    color: #888;" +
                "    text-align: center;" +
                "    padding: 15px;" +
                "    font-size: 14px;" +
                "    border-top: 1px solid #ddd;" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='email-container'>" +
                "<div class='header'>Confirmação de Cadastro</div>" +
                "<div class='content'>" +
                "<p>Olá <strong>" + surname + "</strong>,</p>" +
                "<p>Seu código de ativação é:</p>" +
                "<div class='activation-code'>" + activationCode + "</div>" +
                "<p>Use este código para ativar sua conta. Caso não tenha solicitado este cadastro, por favor ignore este e-mail.</p>" +
                "<p>Atenciosamente,<br>Equipe Neia Salgados</p>" +
                "</div>" +
                "<div class='footer'>© 2024 Neia Salgados. Todos os direitos reservados.</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        emailService.sendEmail(toEmail, "Confirmação de Cadastro", htmlMessage);
    }
}
