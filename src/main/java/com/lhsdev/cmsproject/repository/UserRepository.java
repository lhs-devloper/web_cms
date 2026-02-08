package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.user.AuthProvider;
import com.lhsdev.cmsproject.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);

    java.util.List<User> findByPasswordIsNull();

    java.util.List<User> findByNameContaining(String name);

    java.util.List<User> findByRole(com.lhsdev.cmsproject.domain.user.Role role);

    java.util.List<User> findByNameContainingAndRole(String name, com.lhsdev.cmsproject.domain.user.Role role);
}
