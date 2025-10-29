package org.neiasalgados.repository;

import org.neiasalgados.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByDescriptionContainingIgnoreCase(String description);
    Page<Category> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
}
