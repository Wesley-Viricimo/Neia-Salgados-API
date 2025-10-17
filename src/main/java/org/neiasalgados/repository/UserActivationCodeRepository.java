package org.neiasalgados.repository;

import org.neiasalgados.domain.entity.UserActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivationCodeRepository extends JpaRepository<UserActivationCode, Long> {
}
