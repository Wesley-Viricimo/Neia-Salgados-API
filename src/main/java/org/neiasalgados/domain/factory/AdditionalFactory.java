package org.neiasalgados.domain.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.neiasalgados.domain.dto.ActionAuditingDTO;
import org.neiasalgados.domain.dto.request.AdditionalCreateRequestDTO;
import org.neiasalgados.domain.dto.request.AdditionalUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.AdditionalResponseDTO;
import org.neiasalgados.domain.entity.Additional;
import org.neiasalgados.domain.enums.ChangeType;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.NotFoundException;
import org.neiasalgados.repository.AdditionalRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.neiasalgados.services.AuditingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class AdditionalFactory {
    private final AdditionalRepository additionalRepository;
    private final AuditingService auditingService;
    private final AuthenticationFacade authenticationFacade;
    private final ObjectMapper objectMapper;

    public AdditionalFactory(AdditionalRepository additionalRepository, AuditingService auditingService, AuthenticationFacade authenticationFacade, ObjectMapper objectMapper) {
        this.additionalRepository = additionalRepository;
        this.auditingService = auditingService;
        this.authenticationFacade = authenticationFacade;
        this.objectMapper = objectMapper;
    }

    public Page<Additional> findAllAdditionals(String description, Pageable pageable) {
        return Optional.ofNullable(description)
                .filter(desc -> !desc.isEmpty())
                .map(desc -> this.additionalRepository.findByDescriptionContainingIgnoreCase(desc, pageable))
                .orElseGet(() -> this.additionalRepository.findAll(pageable));
    }

    public Additional createAdditional(AdditionalCreateRequestDTO additionalCreateRequestDTO) {
        if (this.additionalRepository.findByDescriptionContainingIgnoreCase(additionalCreateRequestDTO.getDescription()).isPresent())
            throw new DataIntegrityViolationException(String.format("Já existe um adicional cadastrado com a descrição '%s'", additionalCreateRequestDTO.getDescription()));

        if (additionalCreateRequestDTO.getPrice() <= 0)
            throw new DataIntegrityViolationException("O valor do adicional deve ser maior que zero");

        Additional additional = new Additional(
                additionalCreateRequestDTO.getDescription(),
                additionalCreateRequestDTO.getPrice()
        );

        try {
            String categoryJson = objectMapper.writeValueAsString(new AdditionalResponseDTO(additional));
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    this.authenticationFacade.getAuthenticatedUserId(),
                    "CADASTRO DE ADICIONAL",
                    "ADICIONAL",
                    additional.getIdAdditional(),
                    null,
                    categoryJson,
                    ChangeType.CREATE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        return additional;
    }

    public Additional updateAdditional(AdditionalUpdateRequestDTO additionalUpdateRequestDTO) {
        Additional additional = this.additionalRepository.findById(additionalUpdateRequestDTO.getIdAdditional())
                .orElseThrow(() -> new NotFoundException(String.format("Adicional com ID '%d' não encontrado", additionalUpdateRequestDTO.getIdAdditional())));

        AdditionalResponseDTO originalAdditional = new AdditionalResponseDTO(additional);

        if (additionalUpdateRequestDTO.getDescription() != null) {
            Optional<Additional> existingAdditional = this.additionalRepository.findByDescriptionContainingIgnoreCase(additionalUpdateRequestDTO.getDescription());

            if (existingAdditional.isPresent() && !existingAdditional.get().getIdAdditional().equals(additional.getIdAdditional()))
                throw new DataIntegrityViolationException(String.format("Já existe um adicional cadastrado com a descrição '%s'", additionalUpdateRequestDTO.getDescription()));

            additional.setDescription(additionalUpdateRequestDTO.getDescription());
        }

        if (additionalUpdateRequestDTO.getPrice() != null) {
            if (additionalUpdateRequestDTO.getPrice() <= 0)
                throw new DataIntegrityViolationException("O valor do adicional deve ser maior que zero");

            additional.setPrice(additionalUpdateRequestDTO.getPrice());
        }

        additional.setUpdatedAt(LocalDateTime.now());

        try {
            String previousJson = objectMapper.writeValueAsString(originalAdditional);
            String newJson = objectMapper.writeValueAsString(new AdditionalResponseDTO(additional));

            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    this.authenticationFacade.getAuthenticatedUserId(),
                    "ATUALIZAÇÃO DE ADICIONAL",
                    "ADICIONAL",
                    additional.getIdAdditional(),
                    previousJson,
                    newJson,
                    ChangeType.UPDATE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        return additional;
    }

    public Additional deleteAdditional(Long idAdditional) {
        Additional additional = this.additionalRepository.findById(idAdditional)
                .orElseThrow(() -> new NotFoundException(String.format("Adicional com ID '%d' não encontrado", idAdditional)));

        try {
            String previousJson = objectMapper.writeValueAsString(new AdditionalResponseDTO(additional));
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    this.authenticationFacade.getAuthenticatedUserId(),
                    "EXCLUSÃO DE ADICIONAL",
                    "ADICIONAL",
                    additional.getIdAdditional(),
                    previousJson,
                    null,
                    ChangeType.DELETE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        return additional;
    }

}
