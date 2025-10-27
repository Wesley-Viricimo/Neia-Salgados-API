package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.request.ProductCreateRequestDTO;
import org.neiasalgados.domain.dto.response.ProductResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.enums.UserRole;
import org.neiasalgados.security.annotations.AllowRole;
import org.neiasalgados.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/neiasalgados/api/v1/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
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
}
