package org.neiasalgados.repository;

import org.neiasalgados.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM T_USER u WHERE u.email = :email OR u.phone = :phone OR u.cpf = :cpf")
    List<User> findByEmailOrPhoneOrCpf(String email, String phone, String cpf);
    Optional<User> findByEmail(String email);
    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<User> findByEmailAndIdUserNot(String email, Long userId);
    Optional<User> findByPhoneAndIdUserNot(String phone, Long userId);
    Optional<User> findByCpfAndIdUserNot(String cpf, Long userId);
}
