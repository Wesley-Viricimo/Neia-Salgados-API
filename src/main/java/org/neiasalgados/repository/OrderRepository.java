package org.neiasalgados.repository;

import org.neiasalgados.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT o.id_order, o.id_user, o.id_address, o.order_status, o.payment_method, o.type_of_delivery, o.total_additional, o.total_price, o.delivery_date, o.created_at, o.updated_at FROM neia_salgados.t_order o " +
            "INNER JOIN neia_salgados.t_user u ON o.id_user = u.id_user " +
            "WHERE (:userName IS NULL OR u.name ILIKE CONCAT('%', :userName, '%')) " +
            "AND (:isPending IS NULL OR " +
            "(:isPending = true AND o.order_status NOT IN ('ENTREGUE', 'CANCELADO')) OR " +
            "(:isPending = false AND o.order_status IN ('ENTREGUE', 'CANCELADO')))",
            nativeQuery = true)
    Page<Order> findAllWithFilters(@Param("userName") String userName, @Param("isPending") Boolean isPending, Pageable pageable);
}
