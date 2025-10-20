package org.neiasalgados.exceptions.handler;

import io.jsonwebtoken.MalformedJwtException;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.exceptions.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(Exception ex, WebRequest request) {
        var messageResponse = new MessageResponseDTO("error", "Erro", List.of(ex.getMessage()));
        ExceptionResponse exceptionResponse = new ExceptionResponse(messageResponse, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundException(Exception ex, WebRequest request) {
        var messageResponse = new MessageResponseDTO("error", "Erro", List.of(ex.getMessage()));
        ExceptionResponse exceptionResponse = new ExceptionResponse(messageResponse, HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateFieldsException.class)
    public final ResponseEntity<ExceptionResponse> handleDuplicateFieldsException(DuplicateFieldsException ex) {
        var messageResponse = new MessageResponseDTO("error", "Erro", ex.getDuplicateFields());
        ExceptionResponse exceptionResponse = new ExceptionResponse(messageResponse, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserInactiveException.class)
    public final ResponseEntity<ExceptionResponse> handleUserInactiveException(UserInactiveException ex) {
        var messageResponse = new MessageResponseDTO("error", "Erro", List.of(ex.getMessage()));
        ExceptionResponse exceptionResponse = new ExceptionResponse(messageResponse, HttpStatus.FORBIDDEN.value(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        var messageResponse = new MessageResponseDTO("error", "Erro", List.of(ex.getMessage()));
        ExceptionResponse exceptionResponse = new ExceptionResponse(messageResponse, HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidCredentialsException() {
        var messageResponse = new MessageResponseDTO("error", "Erro", List.of("Usuário ou senha inválidos"));
        ExceptionResponse exceptionResponse = new ExceptionResponse(messageResponse, HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errorDetails = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        var messageResponse = new MessageResponseDTO("error", "Erro", errorDetails);
        ExceptionResponse exceptionResponse = new ExceptionResponse(messageResponse, HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
