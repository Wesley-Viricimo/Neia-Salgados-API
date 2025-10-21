package org.neiasalgados.repository;

import org.neiasalgados.domain.entity.User;
import org.neiasalgados.domain.entity.UserActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserActivationCodeRepository extends JpaRepository<UserActivationCode, Long> {

    Optional<UserActivationCode> findByUserAndCode(User user, String code);
    Optional<UserActivationCode> findByUser(User user);
}
