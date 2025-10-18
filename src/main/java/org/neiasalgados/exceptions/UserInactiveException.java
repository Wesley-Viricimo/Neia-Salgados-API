package org.neiasalgados.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserInactiveException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserInactiveException(String message) {
        super(message);
    }
}
