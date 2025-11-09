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
import org.neiasalgados.domain.factory.AdditionalFactory;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.NotFoundException;
import org.neiasalgados.repository.AdditionalRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdditionalService {
    private final AdditionalRepository additionalRepository;
    private final AdditionalFactory additionalFactory;

    public AdditionalService(AdditionalRepository additionalRepository, AdditionalFactory additionalFactory) {
        this.additionalRepository = additionalRepository;
        this.additionalFactory = additionalFactory;
    }

    public ResponseDataDTO<PageResponseDTO<AdditionalResponseDTO>> findAll(String description, Pageable pageable) {
        Page<Additional> additionalPage = this.additionalFactory.findAllAdditionals(description, pageable);
        Page<AdditionalResponseDTO> additionalDTOPage = additionalPage.map(AdditionalResponseDTO::new);
        PageResponseDTO<AdditionalResponseDTO> pageResponse = new PageResponseDTO<>(additionalDTOPage);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Adicionais listados com sucesso"));
        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
    }

    @Transactional
    public ResponseDataDTO<AdditionalResponseDTO> createAdditional(AdditionalCreateRequestDTO additionalCreateRequestDTO) {
        Additional additional = this.additionalRepository.save(this.additionalFactory.createAdditional(additionalCreateRequestDTO));
        AdditionalResponseDTO additionalResponseDTO = new AdditionalResponseDTO(additional);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Adicional cadastrado com sucesso"));
        return new ResponseDataDTO<>(additionalResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public ResponseDataDTO<AdditionalResponseDTO> updateAdditional(AdditionalUpdateRequestDTO additionalUpdateRequestDTO) {
        Additional additional = this.additionalRepository.save(this.additionalFactory.updateAdditional(additionalUpdateRequestDTO));
        AdditionalResponseDTO additionalResponseDTO = new AdditionalResponseDTO(additional);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Adicional atualizado com sucesso"));
        return new ResponseDataDTO<>(additionalResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public void deleteAdditional(Long idAdditional) {
        Additional additional = this.additionalFactory.deleteAdditional(idAdditional);
        this.additionalRepository.delete(additional);
    }
}
