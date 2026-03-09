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
        private com.lhsdev.cmsproject.repository.MainMenuRepository mainMenuRepository;
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
                                                .clientId("dummy-google-id")
                                                .clientSecret("dummy-google-secret")
                                                .clientAuthenticationMethod("client_secret_basic")
                                                .authorizationGrantType("authorization_code")
                                                .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
                                                .scopes("email,profile")
                                                .clientName("Google")
                                                .build());

                                // Seed Kakao (Placeholder)
                                socialRepository.save(SocialServiceConfig.builder()
                                                .registrationId("kakao")
                                                .clientId("20543122589b138f374315f3bbd94eb6")
                                                .clientSecret("qdGGE9cr0gs6XB8exvWJWuqyA7k864ZM")
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
                                                .clientId("dummy-github-id")
                                                .clientSecret("dummy-github-secret")
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

                        // 3. Main Menu Initialization
                        if (mainMenuRepository.count() == 0) {
                                mainMenuRepository.save(com.lhsdev.cmsproject.domain.menu.MainMenu.builder()
                                                .title("신상품")
                                                .linkUrl("#new")
                                                .sortOrder(0)
                                                .isActive(true)
                                                .build());
                                mainMenuRepository.save(com.lhsdev.cmsproject.domain.menu.MainMenu.builder()
                                                .title("카테고리")
                                                .linkUrl("#categories")
                                                .sortOrder(1)
                                                .isActive(true)
                                                .build());
                                mainMenuRepository.save(com.lhsdev.cmsproject.domain.menu.MainMenu.builder()
                                                .title("베스트")
                                                .linkUrl("#featured")
                                                .sortOrder(2)
                                                .isActive(true)
                                                .build());
                                mainMenuRepository.save(com.lhsdev.cmsproject.domain.menu.MainMenu.builder()
                                                .title("브랜드 스토리")
                                                .linkUrl("#about")
                                                .sortOrder(3)
                                                .isActive(true)
                                                .build());
                        }

                        // 4. Create test user for development
                        if (userRepository.findByEmail("test@test.com").isEmpty()) {
                                userRepository.save(com.lhsdev.cmsproject.domain.user.User.builder()
                                                .name("테스트유저")
                                                .email("test@test.com")
                                                .password(passwordEncoder.encode("1234"))
                                                .role(com.lhsdev.cmsproject.domain.user.Role.USER)
                                                .provider(com.lhsdev.cmsproject.domain.user.AuthProvider.LOCAL)
                                                .build());
                        }
                };
        }
}
