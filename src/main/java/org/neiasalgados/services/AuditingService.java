package org.neiasalgados.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.ActionAuditingDTO;
import org.neiasalgados.domain.dto.DescriptionAuditingDTO;
import org.neiasalgados.domain.entity.Auditing;
import org.neiasalgados.domain.factory.AuditingFactory;
import org.neiasalgados.repository.AuditingRepository;
import org.neiasalgados.repository.UserRepository;
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
