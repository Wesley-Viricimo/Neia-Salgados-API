package org.neiasalgados.domain.entity;

import jakarta.persistence.*;
import org.neiasalgados.domain.enums.ChangeType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "T_AUDITING")
public class Auditing implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AUDITING", length = 16)
    private Long idAuditing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "CHANGE_TYPE", nullable = false, length = 10)
    private ChangeType changeType;

    @Column(name = "OPERATION", nullable = false)
    private String operation;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    public Auditing() {}

    public Auditing(Long userId, ChangeType changeType, String operation, String description) {
        this.user = new User();
        this.user.setIdUser(userId);
        this.changeType = changeType;
        this.operation = operation;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public Long getIdAuditing() {
        return idAuditing;
    }

    public void setIdAuditing(Long idAuditing) {
        this.idAuditing = idAuditing;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}