package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.payment.PaymentConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentConfigRepository extends JpaRepository<PaymentConfig, Long> {
    Optional<PaymentConfig> findByProvider(String provider);
    List<PaymentConfig> findByIsActiveTrue();
}
