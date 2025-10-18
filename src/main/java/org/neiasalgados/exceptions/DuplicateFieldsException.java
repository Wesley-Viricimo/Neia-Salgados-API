package org.neiasalgados.exceptions;

import java.util.List;

public class DuplicateFieldsException extends RuntimeException {
    private final List<String> duplicateFields;

    public DuplicateFieldsException(List<String> duplicateFields) {
        super("Campos já cadastrados no sistema");
        this.duplicateFields = duplicateFields;
    }

    public List<String> getDuplicateFields() {
        return duplicateFields;
    }
}
