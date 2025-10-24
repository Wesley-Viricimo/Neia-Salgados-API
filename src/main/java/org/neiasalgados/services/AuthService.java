package org.neiasalgados.services;

import org.neiasalgados.domain.dto.request.AuthRequestDTO;
import org.neiasalgados.domain.dto.response.AuthResponseDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.entity.User;
import org.neiasalgados.exceptions.UserInactiveException;
import org.neiasalgados.repository.UserRepository;
import org.neiasalgados.security.jwt.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return new UserSecurity(user);
    }

    public ResponseDataDTO<AuthResponseDTO> authenticate(AuthRequestDTO data) {
        User user = userRepository.findByEmail(data.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não está cadastrado no sistema"));

        if (!user.isActive())
            throw new UserInactiveException("Usuário não está ativo. Por favor, verifique seu e-mail para ativar sua conta.");

        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
        var auth = authenticationManager.authenticate(usernamePassword);
        var userSecurity = (UserSecurity) auth.getPrincipal();
        var token = jwtTokenProvider.createToken(userSecurity);

        AuthResponseDTO authResponse = new AuthResponseDTO(token);
        MessageResponseDTO message = new MessageResponseDTO("success", "Sucesso", List.of("Autenticação realizada com sucesso"));
        return new ResponseDataDTO<>(authResponse, message, HttpStatus.OK.value());
    }
}