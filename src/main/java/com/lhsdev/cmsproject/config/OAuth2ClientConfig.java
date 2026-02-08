package com.lhsdev.cmsproject.config;

import com.lhsdev.cmsproject.domain.user.SocialServiceConfig;
import com.lhsdev.cmsproject.repository.SocialServiceConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfig {

    @Autowired
    private SocialServiceConfigRepository socialServiceConfigRepository;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new ClientRegistrationRepository() {
            @Override
            public ClientRegistration findByRegistrationId(String registrationId) {
                return socialServiceConfigRepository.findByRegistrationId(registrationId)
                        .filter(SocialServiceConfig::isActive)
                        .map(OAuth2ClientConfig.this::toClientRegistration)
                        .orElse(null);
            }
        };
    }

    private ClientRegistration toClientRegistration(SocialServiceConfig config) {
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
