package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class ChangeUserActivitieRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "O campo 'userId' não pode ser vazio")
    private Long userId;
    @NotNull(message = "O campo 'active' não pode ser vazio")
    private boolean active;

    public ChangeUserActivitieRequestDTO() {}

    public ChangeUserActivitieRequestDTO(Long userId, boolean active) {
        this.userId = userId;
        this.active = active;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isActive() {
        return active;
    }
}
