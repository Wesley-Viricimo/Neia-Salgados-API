package org.neiasalgados.repository;

import org.neiasalgados.domain.entity.Address;
import org.neiasalgados.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Page<Address> findByUser(User user, Pageable pageable);
}
