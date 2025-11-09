package org.neiasalgados.services;

import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.ActionAuditingDTO;
import org.neiasalgados.domain.factory.AuditingFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditingService {
    private final AuditingFactory auditingFactory;

    public AuditingService(AuditingFactory auditingFactory) {
        this.auditingFactory = auditingFactory;
    }

    @Transactional
    public void saveAudit(ActionAuditingDTO actionAuditingDTO) {
        this.auditingFactory.saveAudit(actionAuditingDTO);
    }
}
