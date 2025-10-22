package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class UpdateUserRoleRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "O campo 'userId' não pode ser vazio")
    private Long userId;
    @NotBlank(message = "O campo 'newRole' não pode ser vazio")
    private String newRole;

    public UpdateUserRoleRequestDTO() {}

    public UpdateUserRoleRequestDTO(Long userId, String newRole) {
        this.userId = userId;
        this.newRole = newRole;
    }

    public Long getUserId() {
        return userId;
    }

    public String getNewRole() {
        return newRole;
    }
}
