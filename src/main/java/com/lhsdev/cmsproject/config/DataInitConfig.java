package com.lhsdev.cmsproject.config;

import com.lhsdev.cmsproject.domain.membership.MembershipGrade;
import com.lhsdev.cmsproject.domain.payment.PaymentConfig;
import com.lhsdev.cmsproject.domain.product.ProductCategory;
import com.lhsdev.cmsproject.domain.user.SocialServiceConfig;
import com.lhsdev.cmsproject.repository.MembershipGradeRepository;
import com.lhsdev.cmsproject.repository.PaymentConfigRepository;
import com.lhsdev.cmsproject.repository.ProductCategoryRepository;
import com.lhsdev.cmsproject.repository.SocialServiceConfigRepository;
import com.lhsdev.cmsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitConfig {

        @Autowired
        private SocialServiceConfigRepository socialRepository;
        @Autowired
        private PaymentConfigRepository paymentConfigRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private com.lhsdev.cmsproject.repository.MainMenuRepository mainMenuRepository;
        @Autowired
        private ProductCategoryRepository productCategoryRepository;
        @Autowired
        private MembershipGradeRepository membershipGradeRepository;
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

                        // 2. Payment Config Init
                        if (paymentConfigRepository.count() == 0) {
                                paymentConfigRepository.save(PaymentConfig.builder()
                                                .provider("KCP")
                                                .clientKey("")
                                                .secretKey("")
                                                .apiUrl("https://stg-spl.kcp.co.kr")
                                                .displayName("NHN KCP")
                                                .isActive(false)
                                                .build());

                                paymentConfigRepository.save(PaymentConfig.builder()
                                                .provider("KAKAOPAY")
                                                .clientKey("")
                                                .secretKey("")
                                                .cid("TC0ONETIME")
                                                .apiUrl("https://open-api.kakaopay.com")
                                                .displayName("카카오페이")
                                                .isActive(false)
                                                .build());

                                paymentConfigRepository.save(PaymentConfig.builder()
                                                .provider("TOSSPAY")
                                                .clientKey("")
                                                .secretKey("")
                                                .apiUrl("https://api.tosspayments.com")
                                                .displayName("토스페이")
                                                .isActive(false)
                                                .build());
                        }

                        // 3. ProductCategory 시드
                        if (productCategoryRepository.count() == 0) {
                                productCategoryRepository.saveAll(List.of(
                                        ProductCategory.builder().code("NORMAL").name("일반상품").description("일반 판매 상품").hasStock(true).hasRentalPeriod(false).sortOrder(0).build(),
                                        ProductCategory.builder().code("RENTAL").name("대여상품").description("대여/렌탈 상품").hasStock(false).hasRentalPeriod(true).sortOrder(1).build(),
                                        ProductCategory.builder().code("DIGITAL").name("디지털상품").description("다운로드 가능한 디지털 콘텐츠").hasStock(false).hasRentalPeriod(false).sortOrder(2).build(),
                                        ProductCategory.builder().code("SUBSCRIPTION").name("구독상품").description("정기 구독 서비스").hasStock(false).hasRentalPeriod(false).sortOrder(3).build(),
                                        ProductCategory.builder().code("SERVICE").name("서비스/예약").description("예약형 서비스 상품").hasStock(false).hasRentalPeriod(false).sortOrder(4).build(),
                                        ProductCategory.builder().code("TICKET").name("이용권").description("입장권, 이벤트 티켓 등").hasStock(true).hasRentalPeriod(false).sortOrder(5).build()
                                ));
                        }

                        // Membership Grade Init
                        if (membershipGradeRepository.count() == 0) {
                                membershipGradeRepository.saveAll(List.of(
                                        MembershipGrade.builder().code("BRONZE").name("브론즈").description("기본 등급").minPoints(0).pointRate(1.0).benefits("기본 적립률 1%").color("#cd7f32").sortOrder(0).build(),
                                        MembershipGrade.builder().code("SILVER").name("실버").description("10,000P 이상").minPoints(10000).pointRate(2.0).benefits("적립률 2%\n무료배송 쿠폰 월 1회").color("#c0c0c0").sortOrder(1).build(),
                                        MembershipGrade.builder().code("GOLD").name("골드").description("50,000P 이상").minPoints(50000).pointRate(3.0).benefits("적립률 3%\n무료배송\n생일 쿠폰").color("#ffd700").sortOrder(2).build(),
                                        MembershipGrade.builder().code("VIP").name("VIP").description("200,000P 이상").minPoints(200000).pointRate(5.0).benefits("적립률 5%\n무료배송\n전용 고객센터\n신상품 우선 구매").color("#8b5cf6").sortOrder(3).build()
                                ));
                        }

                        // 4. Fix legacy users with no password
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
