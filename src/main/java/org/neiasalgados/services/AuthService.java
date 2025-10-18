package org.neiasalgados.services;

import org.neiasalgados.domain.dto.request.AuthRequestDTO;
import org.neiasalgados.domain.dto.response.AuthResponseDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.entity.User;
import org.neiasalgados.exceptions.UserInactiveException;
import org.neiasalgados.repository.UserRepository;
import org.neiasalgados.security.JwtService;
import org.neiasalgados.security.UserSecurity;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, JwtService jwtService, @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new UserSecurity(user);
    }

    public ResponseDataDTO<AuthResponseDTO> authenticate(AuthRequestDTO data) {
        var user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não está cadastrado no sistema"));

        if (!user.isActive())
            throw new UserInactiveException("Usuário não está ativo. Por favor, verifique seu e-mail para ativar sua conta.");

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
        var auth = authenticationManager.authenticate(usernamePassword);
        var userSecurity = (UserSecurity) auth.getPrincipal();
        var token = jwtService.generateToken(userSecurity);

        var authResponse = new AuthResponseDTO(token);
        var message = new MessageResponseDTO("success", "Sucesso", List.of("Autenticação realizada com sucesso"));
        return new ResponseDataDTO<>(authResponse, message, HttpStatus.OK.value());
    }
}
