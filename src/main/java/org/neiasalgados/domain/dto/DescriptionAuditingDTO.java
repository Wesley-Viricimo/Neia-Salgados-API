package org.neiasalgados.domain.dto;

public class DescriptionAuditingDTO {
    private String action;
    private String entity;
    private Object previousValue;
    private Object newValue;

    public DescriptionAuditingDTO(String action, String entity, Object previousValue, Object newValue) {
        this.action = action;
        this.entity = entity;
        this.previousValue = previousValue;
        this.newValue = newValue;
    }

    public String getAction() {
        return action;
    }

    public String getEntity() {
        return entity;
    }

    public Object getPreviousValue() {
        return previousValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}