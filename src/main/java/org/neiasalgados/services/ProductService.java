package org.neiasalgados.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.ActionAuditingDTO;
import org.neiasalgados.domain.dto.request.ProductCreateRequestDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.ProductResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.entity.Category;
import org.neiasalgados.domain.entity.Product;
import org.neiasalgados.domain.enums.ChangeType;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.UnsupportedMediaTypeException;
import org.neiasalgados.repository.CategoryRepository;
import org.neiasalgados.repository.ProductRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuditingService auditingService;
    private final AuthenticationFacade authenticationFacade;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, AuditingService auditingService, AuthenticationFacade authenticationFacade, S3Service s3Service, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.auditingService = auditingService;
        this.authenticationFacade = authenticationFacade;
        this.s3Service = s3Service;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResponseDataDTO<ProductResponseDTO> createProduct(ProductCreateRequestDTO productCreateRequestDTO, MultipartFile multipartFile) throws IOException {
        if (multipartFile != null) {
            if (!multipartFile.getContentType().equals("image/jpeg") && !multipartFile.getContentType().equals("image/png"))
                throw new UnsupportedMediaTypeException("A imagem deve estar no formato JPEG ou PNG.");
        }

        Category category = this.categoryRepository.findById(productCreateRequestDTO.getIdCategory())
                .orElseThrow(() -> new DataIntegrityViolationException(String.format("Categoria com ID %d não encontrada.", productCreateRequestDTO.getIdCategory())));

        if (this.productRepository.findByTitleIgnoreCase(productCreateRequestDTO.getTitle()).isPresent())
            throw new DataIntegrityViolationException(String.format("Já existe um produto cadastrado com o título '%s'", productCreateRequestDTO.getTitle()));

        if (productCreateRequestDTO.getPrice() <= 0)
            throw new DataIntegrityViolationException("O preço do produto deve ser maior que zero.");

        Product productEntity = this.productRepository.save(new Product(
                productCreateRequestDTO.getTitle(),
                productCreateRequestDTO.getDescription(),
                productCreateRequestDTO.getPrice(),
                multipartFile != null ? this.s3Service.uploadFile(multipartFile) : null,
                category));

        try {
            String productJson = objectMapper.writeValueAsString(productEntity);
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    this.authenticationFacade.getAuthenticatedUserId(),
                    "CADASTRO DE PRODUTO",
                    "PRODUTO",
                    productEntity.getIdProduct(),
                    null,
                    productJson,
                    ChangeType.CREATE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(productEntity);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Produto cadastrado com sucesso"));
        return new ResponseDataDTO<>(productResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

}
