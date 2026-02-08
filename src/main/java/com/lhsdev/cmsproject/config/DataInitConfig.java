package com.lhsdev.cmsproject.config;

import com.lhsdev.cmsproject.domain.user.SocialServiceConfig;
import com.lhsdev.cmsproject.repository.SocialServiceConfigRepository;
import com.lhsdev.cmsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitConfig {

        @Autowired
        private SocialServiceConfigRepository socialRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Bean
        public CommandLineRunner dataInit() {
                return args -> {
                        // 1. Social Service Init
                        if (socialRepository.count() == 0) {
                                // Seed Naver (from existing app.yaml)
                                socialRepository.save(SocialServiceConfig.builder()
                                                .registrationId("naver")
                                                .clientId("HdSZDeK8myvxNRSsLgqy")
                                                .clientSecret("jS5eVhfl_5")
                                                .clientAuthenticationMethod("client_secret_post")
                                                .authorizationGrantType("authorization_code")
                                                .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
                                                .scopes("name,email,profile_image")
                                                .clientName("Naver")
                                                .authorizationUri("https://nid.naver.com/oauth2.0/authorize")
                                                .tokenUri("https://nid.naver.com/oauth2.0/token")
                                                .userInfoUri("https://openapi.naver.com/v1/nid/me")
                                                .userNameAttributeName("response")
                                                .build());

                                // Seed Google (Placeholder)
                                socialRepository.save(SocialServiceConfig.builder()
                                                .registrationId("google")
                                                .clientId("YOUR_GOOGLE_CLIENT_ID")
                                                .clientSecret("YOUR_GOOGLE_CLIENT_SECRET")
                                                .clientAuthenticationMethod("client_secret_basic")
                                                .authorizationGrantType("authorization_code")
                                                .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
                                                .scopes("email,profile")
                                                .clientName("Google")
                                                .build());

                                // Seed Kakao (Placeholder)
                                socialRepository.save(SocialServiceConfig.builder()
                                                .registrationId("kakao")
                                                .clientId("YOUR_KAKAO_CLIENT_ID")
                                                .clientSecret("YOUR_KAKAO_CLIENT_SECRET")
                                                .clientAuthenticationMethod("client_secret_post")
                                                .authorizationGrantType("authorization_code")
                                                .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
                                                .scopes("profile_nickname,account_email")
                                                .clientName("Kakao")
                                                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                                                .tokenUri("https://kauth.kakao.com/oauth/token")
                                                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                                                .userNameAttributeName("id")
                                                .build());

                                // Seed Github (Placeholder)
                                socialRepository.save(SocialServiceConfig.builder()
                                                .registrationId("github")
                                                .clientId("YOUR_GITHUB_CLIENT_ID")
                                                .clientSecret("YOUR_GITHUB_CLIENT_SECRET")
                                                .clientAuthenticationMethod("client_secret_basic")
                                                .authorizationGrantType("authorization_code")
                                                .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
                                                .scopes("read:user,user:email")
                                                .clientName("GitHub")
                                                .build());

                                // Seed Apple (Placeholder)
                                socialRepository.save(SocialServiceConfig.builder()
                                                .registrationId("apple")
                                                .clientId("YOUR_APPLE_CLIENT_ID")
                                                .clientSecret("YOUR_APPLE_CLIENT_SECRET")
                                                .clientAuthenticationMethod("client_secret_post")
                                                .authorizationGrantType("authorization_code")
                                                .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
                                                .scopes("name,email")
                                                .clientName("Apple")
                                                .build());
                        }

                        // 2. Fix legacy users with no password
                        java.util.List<com.lhsdev.cmsproject.domain.user.User> usersWithoutPassword = userRepository
                                        .findByPasswordIsNull();
                        if (!usersWithoutPassword.isEmpty()) {
                                System.out.println("Found " + usersWithoutPassword.size()
                                                + " users without password. Updating securely...");
                                for (com.lhsdev.cmsproject.domain.user.User user : usersWithoutPassword) {
                                        String randomPass = java.util.UUID.randomUUID().toString();
                                        user.setPassword(passwordEncoder.encode(randomPass));
                                        userRepository.save(user);
                                }
                                System.out.println("All legacy users updated with secure random passwords.");
                        }
                };
        }
}
