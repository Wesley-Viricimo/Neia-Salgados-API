package org.neiasalgados.domain.entity;

import jakarta.persistence.*;
import org.neiasalgados.domain.enums.NotificationType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "T_NOTIFICATION")
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NOTIFICATION", length = 16)
    private Long idNotification;

    @Column(name = "TITLE", nullable = false, length = 100)
    private String title;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "NOTIFICATION_TYPE", nullable = false, length = 10)
    private NotificationType notificationType;

    @Column(name = "READ", nullable = false)
    private boolean read;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationRead> notificationReads;

    public Notification() {}

    public Notification(String title, String description, NotificationType notificationType) {
        this.title = title;
        this.description = description;
        this.notificationType = notificationType;
        this.read = false;
        this.notificationReads = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public Long getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(Long idNotification) {
        this.idNotification = idNotification;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<NotificationRead> getNotificationReads() {
        return notificationReads;
    }

    public void setNotificationReads(List<NotificationRead> notificationReads) {
        this.notificationReads = notificationReads;
    }
}