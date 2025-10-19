package org.neiasalgados.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ExceptionResponse implements Serializable {
    public static final long serialVersionUID = 1L;
    private MessageResponseDTO message;
    private Integer statusCode;
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private LocalDateTime date;

    public ExceptionResponse(MessageResponseDTO message, Integer statusCode, LocalDateTime date) {
        this.message = message;
        this.statusCode = statusCode;
        this.date = date;
    }

    public MessageResponseDTO getMessage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
