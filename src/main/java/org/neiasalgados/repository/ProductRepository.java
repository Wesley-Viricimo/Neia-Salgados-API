package org.neiasalgados.repository;

import org.neiasalgados.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryIdCategory(Long categoryId);
    Optional<Product> findByTitleIgnoreCase(String title);
}
