package org.neiasalgados.domain.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "T_USER_ACTIVATION_CODE")
public class UserActivationCode implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CODE", length = 16)
    private Long idCode;

    @OneToOne
    @JoinColumn(name = "ID_USER", nullable = false, unique = true)
    private User user;

    @Column(name = "CODE", length = 5, nullable = false)
    private String code;

    @Column(name = "CONFIRMED", nullable = false)
    private boolean confirmed;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    public UserActivationCode() {}

    public UserActivationCode(User user, String code) {
        this.user = user;
        this.code = code;
        this.confirmed = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}