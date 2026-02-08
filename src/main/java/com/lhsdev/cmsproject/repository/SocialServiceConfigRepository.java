package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.user.SocialServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialServiceConfigRepository extends JpaRepository<SocialServiceConfig, Long> {
    Optional<SocialServiceConfig> findByRegistrationId(String registrationId);

    java.util.List<SocialServiceConfig> findByIsActiveTrue();
}
