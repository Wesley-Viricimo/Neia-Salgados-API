package org.neiasalgados.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.ActionAuditingDTO;
import org.neiasalgados.domain.dto.request.AdditionalCreateRequestDTO;
import org.neiasalgados.domain.dto.request.AdditionalUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.AdditionalResponseDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.entity.Additional;
import org.neiasalgados.domain.enums.ChangeType;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.NotFoundException;
import org.neiasalgados.repository.AdditionalRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AdditionalService {
    private final AdditionalRepository additionalRepository;
    private final AuditingService auditingService;
    private final AuthenticationFacade authenticationFacade;
    private final ObjectMapper objectMapper;

    public AdditionalService(AdditionalRepository additionalRepository, AuditingService auditingService, AuthenticationFacade authenticationFacade, ObjectMapper objectMapper) {
        this.additionalRepository = additionalRepository;
        this.auditingService = auditingService;
        this.authenticationFacade = authenticationFacade;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResponseDataDTO<AdditionalResponseDTO> createAdditional(AdditionalCreateRequestDTO additionalCreateRequestDTO) {
        if (this.additionalRepository.findByDescriptionContainingIgnoreCase(additionalCreateRequestDTO.getDescription()).isPresent())
            throw new DataIntegrityViolationException(String.format("Já existe um adicional cadastrado com a descrição '%s'", additionalCreateRequestDTO.getDescription()));

        if (additionalCreateRequestDTO.getPrice() <= 0)
            throw new DataIntegrityViolationException("O valor do adicional deve ser maior que zero");

        Additional additional = this.additionalRepository.save(new Additional(
                additionalCreateRequestDTO.getDescription(),
                additionalCreateRequestDTO.getPrice()
        ));

        try {
            String categoryJson = objectMapper.writeValueAsString(additional);
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

        AdditionalResponseDTO additionalResponseDTO = new AdditionalResponseDTO(additional);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Adicional cadastrado com sucesso"));

        return new ResponseDataDTO<>(additionalResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    public ResponseDataDTO<PageResponseDTO<AdditionalResponseDTO>> findAll(String description, Pageable pageable) {
        Page<Additional> additionalPage = Optional.ofNullable(description)
                .filter(desc -> !desc.isEmpty())
                .map(desc -> this.additionalRepository.findByDescriptionContainingIgnoreCase(desc, pageable))
                .orElseGet(() -> this.additionalRepository.findAll(pageable));

        Page<AdditionalResponseDTO> additionalDTOPage = additionalPage.map(AdditionalResponseDTO::new);
        PageResponseDTO<AdditionalResponseDTO> pageResponse = new PageResponseDTO<>(additionalDTOPage);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Adicionais listados com sucesso"));
        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
    }

    @Transactional
    public ResponseDataDTO<AdditionalResponseDTO> updateAdditional(AdditionalUpdateRequestDTO additionalUpdateRequestDTO) {
        Additional additional = this.additionalRepository.findById(additionalUpdateRequestDTO.getIdAdditional())
                .orElseThrow(() -> new NotFoundException(String.format("Adicional com ID '%d' não encontrado", additionalUpdateRequestDTO.getIdAdditional())));

        Additional newAdditional = additional;

        if (additionalUpdateRequestDTO.getDescription() != null && !additionalUpdateRequestDTO.getDescription().isEmpty()) {
            Optional<Additional> existingAdditional = this.additionalRepository.findByDescriptionContainingIgnoreCase(additionalUpdateRequestDTO.getDescription());

            if (existingAdditional.isPresent() && !existingAdditional.get().getIdAdditional().equals(additional.getIdAdditional()))
                throw new DataIntegrityViolationException(String.format("Já existe um adicional cadastrado com a descrição '%s'", additionalUpdateRequestDTO.getDescription()));

            newAdditional.setDescription(additionalUpdateRequestDTO.getDescription());
        }

        if (additionalUpdateRequestDTO.getPrice() != null) {
            if (additionalUpdateRequestDTO.getPrice() <= 0)
                throw new DataIntegrityViolationException("O valor do adicional deve ser maior que zero");
            newAdditional.setPrice(additionalUpdateRequestDTO.getPrice());
        }

        try {
            String previousJson = objectMapper.writeValueAsString(additional);
            this.additionalRepository.save(newAdditional);
            String newJson = objectMapper.writeValueAsString(newAdditional);

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

        AdditionalResponseDTO additionalResponseDTO = new AdditionalResponseDTO(additional);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Adicional atualizado com sucesso"));

        return new ResponseDataDTO<>(additionalResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public void deleteAdditional(Long idAdditional) {
        Additional additional = this.additionalRepository.findById(idAdditional)
                .orElseThrow(() -> new NotFoundException(String.format("Adicional com ID '%d' não encontrado", idAdditional)));

        try {
            String previousJson = objectMapper.writeValueAsString(additional);
            this.additionalRepository.delete(additional);

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

        this.additionalRepository.deleteById(idAdditional);
    }
}
