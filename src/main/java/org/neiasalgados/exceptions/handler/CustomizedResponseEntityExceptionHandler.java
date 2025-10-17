package org.neiasalgados.exceptions.handler;

import jakarta.validation.ValidationException;
import org.neiasalgados.domain.dto.MessageResponseDTO;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.ExceptionResponse;
import org.neiasalgados.exceptions.NotFoundException;
import org.neiasalgados.exceptions.ValidationFieldsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
        ExceptionResponse exceptionResponse = new ExceptionResponse(messageResponse, 400, LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundException(Exception ex, WebRequest request) {
        var messageResponse = new MessageResponseDTO("error", "Erro", List.of(ex.getMessage()));
        ExceptionResponse exceptionResponse = new ExceptionResponse(messageResponse, 400, LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errorDetails = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        var messageResponse = new MessageResponseDTO("error", "Erro de Validação", errorDetails);
        ExceptionResponse exceptionResponse = new ExceptionResponse(messageResponse, 400, LocalDateTime.now());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
