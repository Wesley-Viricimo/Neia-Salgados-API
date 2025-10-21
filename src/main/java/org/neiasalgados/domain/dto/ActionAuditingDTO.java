package org.neiasalgados.domain.dto;

import org.neiasalgados.domain.enums.ChangeType;

public class ActionAuditingDTO {

    private Long idUser;
    private String action;
    private String entityType;
    private Long entityId;
    private String previousValue;
    private String newValue;
    private ChangeType changeType;

    public ActionAuditingDTO(Long idUser, String action, String entityType, Long entityId, String previousValue, String newValue, ChangeType changeType) {
        this.idUser = idUser;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.changeType = changeType;
    }

    public Long getIdUser() {
        return idUser;
    }

    public String getAction() {
        return action;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public ChangeType getChangeType() {
        return changeType;
    }
}
