package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByPgTransactionId(String pgTransactionId);

    @Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId AND p.status = com.lhsdev.cmsproject.domain.payment.PaymentStatus.PAID ORDER BY p.paidAt DESC LIMIT 1")
    Optional<Payment> findLatestPaidByUserId(@Param("userId") Long userId);
}
