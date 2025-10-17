package org.neiasalgados.repository;

import org.neiasalgados.domain.entity.Auditing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditingRepository extends JpaRepository<Auditing, Long> {
}
