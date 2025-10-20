package org.neiasalgados.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.exceptions.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        var messageResponse = new MessageResponseDTO("error", "Erro", List.of("Acesso negado. Credenciais inválidas ou ausentes."));
        var exceptionResponse = new ExceptionResponse(messageResponse, HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
        response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
    }
}
