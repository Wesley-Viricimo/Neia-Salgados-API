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
import org.neiasalgados.domain.entity.Product;
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
        if (this.categoryRepository.findByDescriptionContainingIgnoreCase(categoryRequestDTO.getDescription()).isPresent())
            throw new DataIntegrityViolationException(String.format("Já existe uma categoria cadastrada com a descrição '%s'", categoryRequestDTO.getDescription()));

        Category categoryEntity = new Category(categoryRequestDTO.getDescription());
        this.categoryRepository.save(categoryEntity);

        try {
            String categoryJson = objectMapper.writeValueAsString(categoryEntity);
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    this.authenticationFacade.getAuthenticatedUserId(),
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

        CategoryResponseDTO categoryDTO = new CategoryResponseDTO(categoryEntity);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Categoria cadastrada com sucesso"));

        return new ResponseDataDTO<>(categoryDTO, messageResponse, HttpStatus.CREATED.value());
    }

    public ResponseDataDTO<PageResponseDTO<CategoryResponseDTO>> findAll(String description, Pageable pageable) {
        Page<Category> categoryPage = Optional.ofNullable(description)
                .filter(desc -> !desc.isEmpty())
                .map(desc -> this.categoryRepository.findByDescriptionContainingIgnoreCase(desc.toUpperCase(), pageable))
                .orElseGet(() -> this.categoryRepository.findAll(pageable));

        Page<CategoryResponseDTO> categoryDTOPage = categoryPage.map(CategoryResponseDTO::new);
        PageResponseDTO<CategoryResponseDTO> pageResponse = new PageResponseDTO<>(categoryDTOPage);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Categorias listadas com sucesso"));
        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
    }

    @Transactional
    public ResponseDataDTO<CategoryResponseDTO> updateCategory(CategoryRequestDTO categoryRequestDTO, Long idCategory) {
        Category category = this.categoryRepository.findById(idCategory).orElseThrow(() ->
                new NotFoundException(String.format("Categoria com id '%d' não encontrada", idCategory))
        );

        Optional<Category> existingCategory = this.categoryRepository.findByDescriptionContainingIgnoreCase(categoryRequestDTO.getDescription());

        if (existingCategory.isPresent() && !existingCategory.get().getIdCategory().equals(idCategory))
            throw new DataIntegrityViolationException(String.format("Já existe uma categoria cadastrada com a descrição '%s'", categoryRequestDTO.getDescription()));

        try {
            String previousJson = objectMapper.writeValueAsString(category);

            category.setDescription(categoryRequestDTO.getDescription());
            this.categoryRepository.save(category);

            String newJson = objectMapper.writeValueAsString(category);

            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    this.authenticationFacade.getAuthenticatedUserId(),
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

        CategoryResponseDTO categoryDTO = new CategoryResponseDTO(category);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Categoria atualizada com sucesso"));

        return new ResponseDataDTO<>(categoryDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public void deleteCategory(Long idCategory) {
        Category category = this.categoryRepository.findById(idCategory)
                .orElseThrow(() -> new NotFoundException(String.format("Categoria com id '%d' não encontrada", idCategory)));

        List<Product> products = this.productRepository.findByCategoryIdCategory(idCategory);

        if (!products.isEmpty())
            throw new DataIntegrityViolationException(String.format("Não é possível excluir a categoria '%s' pois existem produtos vinculados a ela", category.getDescription()));

        try {
            String categoryJson = objectMapper.writeValueAsString(category);
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    this.authenticationFacade.getAuthenticatedUserId(),
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

        this.categoryRepository.deleteById(idCategory);
    }
}
