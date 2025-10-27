package org.neiasalgados.repository;

import org.neiasalgados.domain.entity.Additional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdditionalRepository extends JpaRepository<Additional, Long> {
    Optional<Additional> findByDescriptionContainingIgnoreCase(String description);
    Page<Additional> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
}
