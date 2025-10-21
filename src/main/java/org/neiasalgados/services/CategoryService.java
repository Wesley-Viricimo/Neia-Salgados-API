package org.neiasalgados.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.ActionAuditingDTO;
import org.neiasalgados.domain.dto.response.CategoryResponseDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.entity.Category;
import org.neiasalgados.domain.dto.request.CategoryRequestDTO;
import org.neiasalgados.domain.enums.ChangeType;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.NotFoundException;
import org.neiasalgados.repository.CategoryRepository;
import org.neiasalgados.repository.ProductRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AuditingService auditingService;
    private final AuthenticationFacade authenticationFacade;
    private final ObjectMapper objectMapper;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository, AuditingService auditingService, AuthenticationFacade authenticationFacade, ObjectMapper objectMapper) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.auditingService = auditingService;
        this.authenticationFacade = authenticationFacade;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResponseDataDTO<CategoryResponseDTO> createCategory(CategoryRequestDTO categoryRequestDTO) {
        String upperDescription = categoryRequestDTO.getDescription().toUpperCase();

        if (this.categoryRepository.findByDescription(upperDescription).isPresent())
            throw new DataIntegrityViolationException(String.format("Já existe uma categoria cadastrada com a descrição '%s'", upperDescription));

        var categoryEntity = new Category(upperDescription);
        this.categoryRepository.save(categoryEntity);

        try {
            String categoryJson = objectMapper.writeValueAsString(categoryEntity);
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    authenticationFacade.getAuthenticatedUserId(),
                    "CADASTRO DE CATEGORIA",
                    "CATEGORIA",
                    categoryEntity.getIdCategory(),
                    null,
                    categoryJson,
                    ChangeType.CREATE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        var categoryDTO = new CategoryResponseDTO(categoryEntity.getIdCategory(), categoryEntity.getDescription());
        var messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Categoria cadastrada com sucesso"));

        return new ResponseDataDTO<>(categoryDTO, messageResponse, HttpStatus.CREATED.value());
    }

    public ResponseDataDTO<PageResponseDTO<CategoryResponseDTO>> findAll(String description, Pageable pageable) {
        Page<Category> categoryPage = Optional.ofNullable(description)
                .filter(desc -> !desc.isEmpty())
                .map(desc -> categoryRepository.findByDescriptionContaining(desc.toUpperCase(), pageable))
                .orElseGet(() -> categoryRepository.findAll(pageable));

        Page<CategoryResponseDTO> categoryDTOPage = categoryPage.map(category -> new CategoryResponseDTO(category.getIdCategory(), category.getDescription()));

        var pageResponse = new PageResponseDTO<>(categoryDTOPage);
        var messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Categorias listadas com sucesso"));

        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
    }

    @Transactional
    public ResponseDataDTO<CategoryResponseDTO> updateCategory(CategoryRequestDTO categoryRequestDTO, Long idCategory) {
        var category = categoryRepository.findById(idCategory).orElseThrow(() ->
                new NotFoundException(String.format("Categoria com id '%d' não encontrada", idCategory))
        );

        String upperDescription = categoryRequestDTO.getDescription().toUpperCase();
        Optional<Category> existingCategory = categoryRepository.findByDescription(upperDescription);

        if (existingCategory.isPresent() && !existingCategory.get().getIdCategory().equals(idCategory))
            throw new DataIntegrityViolationException(String.format("Já existe uma categoria cadastrada com a descrição '%s'", upperDescription));

        try {
            String previousJson = objectMapper.writeValueAsString(category);

            category.setDescription(upperDescription);
            categoryRepository.save(category);

            String newJson = objectMapper.writeValueAsString(category);

            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    authenticationFacade.getAuthenticatedUserId(),
                    "ATUALIZAÇÃO DE CATEGORIA",
                    "CATEGORIA",
                    category.getIdCategory(),
                    previousJson,
                    newJson,
                    ChangeType.UPDATE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        var categoryDTO = new CategoryResponseDTO(category.getIdCategory(), category.getDescription());
        var messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Categoria atualizada com sucesso"));

        return new ResponseDataDTO<>(categoryDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public void deleteCategory(Long idCategory) {
        var category = categoryRepository.findById(idCategory)
                .orElseThrow(() -> new NotFoundException(String.format("Categoria com id '%d' não encontrada", idCategory)));

        var products = productRepository.findByCategoryIdCategory(idCategory);

        if (!products.isEmpty())
            throw new DataIntegrityViolationException(String.format("Não é possível excluir a categoria '%s' pois existem produtos vinculados a ela", category.getDescription()));

        try {
            String categoryJson = objectMapper.writeValueAsString(category);
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    authenticationFacade.getAuthenticatedUserId(),
                    "EXCLUSÃO DE CATEGORIA",
                    "CATEGORIA",
                    category.getIdCategory(),
                    categoryJson,
                    null,
                    ChangeType.DELETE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        categoryRepository.deleteById(idCategory);
    }
}
