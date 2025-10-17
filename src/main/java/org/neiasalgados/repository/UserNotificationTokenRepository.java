package org.neiasalgados.repository;

import org.neiasalgados.domain.entity.UserNotificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationTokenRepository extends JpaRepository<UserNotificationToken, Long> {
}
