package org.neiasalgados.exceptions;

public class ValidationFieldsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ValidationFieldsException(String message) {
        super(message);
    }
}
