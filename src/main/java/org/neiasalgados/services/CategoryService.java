package org.neiasalgados.services;

import jakarta.transaction.Transactional;
import org.neiasalgados.domain.dto.response.CategoryResponseDTO;
import org.neiasalgados.domain.dto.response.MessageResponseDTO;
import org.neiasalgados.domain.dto.response.PageResponseDTO;
import org.neiasalgados.domain.dto.response.ResponseDataDTO;
import org.neiasalgados.domain.entity.Category;
import org.neiasalgados.domain.dto.request.CategoryRequestDTO;
import org.neiasalgados.exceptions.DataIntegrityViolationException;
import org.neiasalgados.exceptions.NotFoundException;
import org.neiasalgados.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ResponseDataDTO<CategoryResponseDTO> createCategory(CategoryRequestDTO categoryRequestDTO) {
        String upperDescription = categoryRequestDTO.getDescription().toUpperCase();

        if (this.categoryRepository.findByDescription(upperDescription).isPresent())
            throw new DataIntegrityViolationException(String.format("Já existe uma categoria cadastrada com a descrição '%s'", upperDescription));

        var categoryEntity = new Category(upperDescription);
        this.categoryRepository.save(categoryEntity);

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

        category.setDescription(upperDescription);
        categoryRepository.save(category);

        var categoryDTO = new CategoryResponseDTO(category.getIdCategory(), category.getDescription());
        var messageResponse = new MessageResponseDTO("success", "Sucesso", List.of("Categoria atualizada com sucesso"));

        return new ResponseDataDTO<>(categoryDTO, messageResponse, HttpStatus.CREATED.value());
    }

    @Transactional
    public void deleteCategory(Long idCategory) {
        categoryRepository.findById(idCategory)
                .orElseThrow(() -> new NotFoundException(String.format("Categoria com id '%d' não encontrada", idCategory)));

        categoryRepository.deleteById(idCategory);
    }
}
