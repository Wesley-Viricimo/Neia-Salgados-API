package org.neiasalgados.controller;

import jakarta.validation.Valid;
import org.neiasalgados.domain.dto.CategoryDTO;
import org.neiasalgados.domain.dto.PageResponseDTO;
import org.neiasalgados.domain.dto.ResponseDataDTO;
import org.neiasalgados.domain.vo.CategoryVO;
import org.neiasalgados.services.CategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/neiasalgados/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ResponseDataDTO<CategoryDTO>> create(@Valid @RequestBody CategoryVO categoryVO) {
        ResponseDataDTO<CategoryDTO> response = categoryService.createCategory(categoryVO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<ResponseDataDTO<PageResponseDTO<CategoryDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "description", required = false) String description
    ) {
        var direcao = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable paginacao = PageRequest.of(page, size, Sort.by(direcao, "description"));
        return ResponseEntity.ok(categoryService.findAll(description, paginacao));
    }

    @PutMapping(value = "/{idCategory}")
    public ResponseEntity<ResponseDataDTO<CategoryDTO>> updateCategory( @Valid @RequestBody CategoryVO categoryVO, @PathVariable(value = "idCategory") Long idCategory) {
        ResponseDataDTO<CategoryDTO> response = categoryService.updateCategory(categoryVO, idCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping(value = "/{idCategory}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long idCategory) {
        categoryService.deleteCategory(idCategory);
        return ResponseEntity.noContent().build();
    }
}
