package org.neiasalgados.domain.dto;

import java.io.Serializable;

public class ResponseDataDTO<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T data;

    private MessageResponseDTO message;

    private Integer statusCode;

    public ResponseDataDTO(T data, MessageResponseDTO message, Integer statusCode ) {
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public MessageResponseDTO getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
