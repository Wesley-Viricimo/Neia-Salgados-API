package org.neiasalgados.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.ActionAuditingDTO;
import org.neiasalgados.domain.dto.DescriptionAuditingDTO;
import org.neiasalgados.domain.entity.Auditing;
import org.neiasalgados.repository.AuditingRepository;
import org.neiasalgados.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditingService {
    private final AuditingRepository auditingRepository;
    private final ObjectMapper objectMapper;

    public AuditingService(AuditingRepository auditingRepository, ObjectMapper objectMapper) {
        this.auditingRepository = auditingRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void saveAudit(ActionAuditingDTO actionAuditingDTO) {
        try {
            Object previousValueObj = actionAuditingDTO.getPreviousValue() != null ?
                    objectMapper.readTree(actionAuditingDTO.getPreviousValue()) :
                    objectMapper.createObjectNode();

            Object newValueObj = actionAuditingDTO.getNewValue() != null ?
                    objectMapper.readTree(actionAuditingDTO.getNewValue()) :
                    objectMapper.createObjectNode();

            DescriptionAuditingDTO descriptionAuditingDTO = new DescriptionAuditingDTO(
                    actionAuditingDTO.getAction(),
                    actionAuditingDTO.getEntityType(),
                    previousValueObj,
                    newValueObj
            );

            String description = objectMapper.writeValueAsString(descriptionAuditingDTO);
            var auditing = new Auditing(
                    actionAuditingDTO.getIdUser(),
                    actionAuditingDTO.getChangeType(),
                    actionAuditingDTO.getAction(),
                    description
            );

            this.auditingRepository.save(auditing);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
