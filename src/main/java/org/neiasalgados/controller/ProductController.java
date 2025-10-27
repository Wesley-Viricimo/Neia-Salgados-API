package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.request.ProductCreateRequestDTO;
import org.neiasalgados.domain.dto.request.ProductUpdateRequestDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ProductResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.security.annotations.AllowRole;
import org.neiasalgados.services.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/neiasalgados/api/v1/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ResponseDataDTO<PageResponseDTO<ProductResponseDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "title", required = false) String title
    ) {
        var dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "title"));
        return ResponseEntity.ok(this.productService.findAll(title, pageable));
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR, UserRole.COMERCIAL})
    @PostMapping
    public ResponseEntity<ResponseDataDTO<ProductResponseDTO>> create(
            @RequestPart("product") @Valid ProductCreateRequestDTO productCreateRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile image
    ) throws IOException {
        ResponseDataDTO<ProductResponseDTO> response = productService.createProduct(productCreateRequestDTO, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR, UserRole.COMERCIAL})
    @PatchMapping
    public ResponseEntity<ResponseDataDTO<ProductResponseDTO>> update(
            @RequestPart("product") @Valid ProductUpdateRequestDTO productUpdateRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile image
    ) throws IOException {
        ResponseDataDTO<ProductResponseDTO> response = productService.updateProduct(productUpdateRequestDTO, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowRole(allowedRoles = {UserRole.DESENVOLVEDOR, UserRole.ADMINISTRADOR, UserRole.COMERCIAL})
    @DeleteMapping(value = "/{idProduct}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long idProduct) {
        this.productService.deleteProduct(idProduct);
        return ResponseEntity.noContent().build();
    }
}
