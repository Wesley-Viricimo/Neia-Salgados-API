package org.neiasalgados.domain.dto.request;

import jakarta.validation.constraints.NotNull;

public class ChangeUserActivitieRequestDTO {
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
