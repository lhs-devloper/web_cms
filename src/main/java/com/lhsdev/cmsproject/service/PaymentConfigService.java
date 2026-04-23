package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.payment.PaymentConfig;
import com.lhsdev.cmsproject.repository.PaymentConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentConfigService {

    private final PaymentConfigRepository repository;

    public List<PaymentConfig> findAll() {
        return repository.findAll();
    }

    public List<PaymentConfig> findActive() {
        return repository.findByIsActiveTrue();
    }

    public Optional<PaymentConfig> findByProvider(String provider) {
        return repository.findByProvider(provider);
    }

    @Transactional
    public PaymentConfig save(PaymentConfig config) {
        return repository.save(config);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
