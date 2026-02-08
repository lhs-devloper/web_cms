package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.user.SocialServiceConfig;
import com.lhsdev.cmsproject.repository.SocialServiceConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialServiceConfigService {

    private final SocialServiceConfigRepository repository;

    public List<SocialServiceConfig> findAll() {
        return repository.findAll();
    }

    public Optional<SocialServiceConfig> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<SocialServiceConfig> findByRegistrationId(String registrationId) {
        return repository.findByRegistrationId(registrationId);
    }

    @Transactional
    public SocialServiceConfig save(SocialServiceConfig config) {
        return repository.save(config);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // Helper to convert Entity to ClientRegistration
    public ClientRegistration toClientRegistration(SocialServiceConfig config) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(config.getRegistrationId())
                .clientId(config.getClientId())
                .clientSecret(config.getClientSecret())
                .clientAuthenticationMethod(new ClientAuthenticationMethod(config.getClientAuthenticationMethod()))
                .authorizationGrantType(new AuthorizationGrantType(config.getAuthorizationGrantType()))
                .redirectUri(config.getRedirectUri())
                .scope(config.getScopes() != null ? config.getScopes().split(",") : null)
                .clientName(config.getClientName());

        if (config.getAuthorizationUri() != null)
            builder.authorizationUri(config.getAuthorizationUri());
        if (config.getTokenUri() != null)
            builder.tokenUri(config.getTokenUri());
        if (config.getUserInfoUri() != null)
            builder.userInfoUri(config.getUserInfoUri());
        if (config.getUserNameAttributeName() != null)
            builder.userNameAttributeName(config.getUserNameAttributeName());
        if (config.getJwkSetUri() != null)
            builder.jwkSetUri(config.getJwkSetUri());
        if (config.getIssuerUri() != null)
            builder.issuerUri(config.getIssuerUri());

        return builder.build();
    }
}
