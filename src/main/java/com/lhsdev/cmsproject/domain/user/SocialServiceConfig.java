package com.lhsdev.cmsproject.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "social_service_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialServiceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String registrationId; // e.g., google, naver, kakao

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String clientSecret;

    private String clientAuthenticationMethod; // e.g., client_secret_post
    private String authorizationGrantType; // e.g., authorization_code
    private String redirectUri;
    private String scopes; // Comma separated, e.g., "email,profile"
    private String clientName;

    // Provider Details
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private String userNameAttributeName;
    private String jwkSetUri;
    private String issuerUri;

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = true;

    public void update(String clientId, String clientSecret, String redirectUri, String scopes, String clientName) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scopes = scopes;
        this.clientName = clientName;
    }
}
