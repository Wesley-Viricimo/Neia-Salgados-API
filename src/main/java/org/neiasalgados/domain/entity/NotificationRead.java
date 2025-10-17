package org.neiasalgados.domain.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "T_NOTIFICATION_READ")
public class NotificationRead implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NOTIFICATION_READ", length = 16)
    private Long idNotificationRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_NOTIFICATION", nullable = false)
    private Notification notification;

    @Column(name = "READ_AT", nullable = false)
    private LocalDateTime readAt;

    public NotificationRead() {}

    public NotificationRead(User user, Notification notification) {
        this.user = user;
        this.notification = notification;
        this.readAt = LocalDateTime.now();
    }

    public Long getIdNotificationRead() {
        return idNotificationRead;
    }

    public void setIdNotificationRead(Long idNotificationRead) {
        this.idNotificationRead = idNotificationRead;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}