package org.neiasalgados.domain.dto.response;

import java.io.Serializable;

public class AuthResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String token;

    public AuthResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
