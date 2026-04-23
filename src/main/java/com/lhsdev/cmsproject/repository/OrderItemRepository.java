package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId " +
            "AND oi.order.status NOT IN ('CANCELLED', 'RETURNED') " +
            "AND oi.rentalStartDate <= :endDate AND oi.rentalEndDate >= :startDate")
    List<OrderItem> findOverlappingRentals(@Param("productId") Long productId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
}
