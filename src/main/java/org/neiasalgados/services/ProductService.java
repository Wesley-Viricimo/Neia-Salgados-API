package org.neiasalgados.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.ActionAuditingDTO;
import org.neiasalgados.domain.dto.request.ProductCreateRequestDTO;
import org.neiasalgados.domain.dto.request.ProductUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ProductResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.entity.Category;
import org.neiasalgados.domain.entity.Product;
import org.neiasalgados.domain.enums.ChangeType;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.NotFoundException;
import org.neiasalgados.exceptions.UnsupportedMediaTypeException;
import org.neiasalgados.repository.CategoryRepository;
import org.neiasalgados.repository.ProductRepository;
import org.neiasalgados.security.AuthenticationFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public ResponseDataDTO<PageResponseDTO<ProductResponseDTO>> findAll(String title, Pageable pageable) {
        Page<Product> productPage = Optional.ofNullable(title)
                .filter(tte -> !tte.isEmpty())
                .map(tte -> this.productRepository.findByTitleContainingIgnoreCase(tte, pageable))
                .orElseGet(() -> this.productRepository.findAll(pageable));

        Page<ProductResponseDTO> productResponseDTOPage = productPage.map(ProductResponseDTO::new);
        PageResponseDTO<ProductResponseDTO> pageResponse = new PageResponseDTO<>(productResponseDTOPage);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Produtos listados com sucesso"));
        return new ResponseDataDTO<>(pageResponse, messageResponse, HttpStatus.OK.value());
    }

    @Transactional
    public ResponseDataDTO<ProductResponseDTO> createProduct(ProductCreateRequestDTO productCreateRequestDTO, MultipartFile multipartFile) throws IOException {
        if (multipartFile != null) {
            if (!multipartFile.getContentType().equals("image/jpeg") && !multipartFile.getContentType().equals("image/png"))
                throw new UnsupportedMediaTypeException("A imagem deve estar no formato JPEG ou PNG.");
        }

        Category category = this.categoryRepository.findById(productCreateRequestDTO.getIdCategory())
                .orElseThrow(() -> new DataIntegrityViolationException(String.format("Categoria com ID %d não encontrada.", productCreateRequestDTO.getIdCategory())));

        if (this.productRepository.findByTitleContainingIgnoreCase(productCreateRequestDTO.getTitle()).isPresent())
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
            String productJson = objectMapper.writeValueAsString(new ProductResponseDTO(productEntity));
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

    @Transactional
    public ResponseDataDTO<ProductResponseDTO> updateProduct(ProductUpdateRequestDTO productUpdateRequestDTO, MultipartFile multipartFile) throws IOException {
        Product product = this.productRepository.findById(productUpdateRequestDTO.getIdProduct())
                .orElseThrow(() -> new NotFoundException(String.format("Produto com ID %d não encontrado.", productUpdateRequestDTO.getIdProduct())));

        if (multipartFile != null) {
            if (!multipartFile.getContentType().equals("image/jpeg") && !multipartFile.getContentType().equals("image/png"))
                throw new UnsupportedMediaTypeException("A imagem deve estar no formato JPEG ou PNG.");
        }

        ProductResponseDTO originalProduct = new ProductResponseDTO(product);

        if (productUpdateRequestDTO.getTitle() != null) {
            Optional<Product> productExists = this.productRepository.findByTitleContainingIgnoreCase(productUpdateRequestDTO.getTitle());
            if (productExists.isPresent() && !productExists.get().getIdProduct().equals(productUpdateRequestDTO.getIdProduct()))
                throw new DataIntegrityViolationException(String.format("Já existe um produto cadastrado com o título '%s'", productUpdateRequestDTO.getTitle()));

            product.setTitle(productUpdateRequestDTO.getTitle());
        }

        if (productUpdateRequestDTO.getDescription() != null)
            product.setDescription(productUpdateRequestDTO.getDescription());

        if (productUpdateRequestDTO.getPrice() != null) {
            if (productUpdateRequestDTO.getPrice() <= 0)
                throw new DataIntegrityViolationException("O preço do produto deve ser maior que zero.");

            product.setPrice(productUpdateRequestDTO.getPrice());
        }

        if (productUpdateRequestDTO.getIdCategory() != null) {
            Category category = this.categoryRepository.findById(productUpdateRequestDTO.getIdCategory())
                    .orElseThrow(() -> new DataIntegrityViolationException(String.format("Categoria com ID %d não encontrada.", productUpdateRequestDTO.getIdCategory())));

            product.setCategory(category);
        }

        if (multipartFile != null) {
            if (product.getUrlImage() != null)
                this.s3Service.deleteFile(product.getUrlImage());

            String newImageUrl = this.s3Service.uploadFile(multipartFile);
            product.setUrlImage(newImageUrl);
        }

        this.productRepository.save(product);

        try {
            String previousJson = objectMapper.writeValueAsString(originalProduct);
            String newJson = objectMapper.writeValueAsString(new ProductResponseDTO(product));

            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    this.authenticationFacade.getAuthenticatedUserId(),
                    "ATUALIZAÇÃO DE PRODUTO",
                    "PRODUTO",
                    product.getIdProduct(),
                    previousJson,
                    newJson,
                    ChangeType.UPDATE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(product);
        MessageResponseDTO messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Produto atualizado com sucesso"));
        return new ResponseDataDTO<>(productResponseDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public void deleteProduct(Long idProduct) {
        Product product = this.productRepository.findById(idProduct)
                .orElseThrow(() -> new NotFoundException(String.format("Produto com ID %d não encontrado.", idProduct)));

        if (product.getUrlImage() != null)
            this.s3Service.deleteFile(product.getUrlImage());

        try {
            String productJson = objectMapper.writeValueAsString(new ProductResponseDTO(product));
            ActionAuditingDTO actionAuditingDTO = new ActionAuditingDTO(
                    this.authenticationFacade.getAuthenticatedUserId(),
                    "EXCLUSÃO DE PRODUTO",
                    "PRODUTO",
                    product.getIdProduct(),
                    productJson,
                    null,
                    ChangeType.DELETE
            );

            this.auditingService.saveAudit(actionAuditingDTO);
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }

        this.productRepository.delete(product);
    }
}
