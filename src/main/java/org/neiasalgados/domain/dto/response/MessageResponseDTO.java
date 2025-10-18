package org.neiasalgados.domain.dto.response;

import java.io.Serializable;
import java.util.List;

public class MessageResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String severity;

    private String message;

    private List<String> details;

    public MessageResponseDTO() {}

    public MessageResponseDTO(String severity, String message, List<String> details) {
        this.severity = severity;
        this.message = message;
        this.details = details;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
