package com.lhsdev.cmsproject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "Bearer Token";

        return new OpenAPI()
                .info(new Info()
                        .title("CMS Project API")
                        .description("통합 웹 CMS 및 커머스 플랫폼 API 문서입니다.\n\n"
                                + "인증이 필요한 API는 우측 상단의 **Authorize** 버튼을 클릭하여 JWT 토큰을 입력하세요.\n\n"
                                + "토큰 형식: `Bearer {token}`")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CMS Project")
                                .email("admin@cmsproject.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 인증 토큰을 입력하세요. (Bearer 접두사 불필요)")));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("01. 인증 (Auth)")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .group("02. 상품 (Product)")
                .pathsToMatch("/api/products/**")
                .build();
    }

    @Bean
    public GroupedOpenApi cartApi() {
        return GroupedOpenApi.builder()
                .group("03. 장바구니 (Cart)")
                .pathsToMatch("/api/cart/**")
                .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("04. 주문 (Order)")
                .pathsToMatch("/api/orders/**")
                .build();
    }

    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
                .group("05. 결제 (Payment)")
                .pathsToMatch("/api/payments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi boardApi() {
        return GroupedOpenApi.builder()
                .group("06. 게시판 (Board)")
                .pathsToMatch("/api/board/**")
                .build();
    }

    @Bean
    public GroupedOpenApi profileApi() {
        return GroupedOpenApi.builder()
                .group("07. 프로필 (Profile)")
                .pathsToMatch("/api/profile/**")
                .build();
    }

    @Bean
    public GroupedOpenApi globalApi() {
        return GroupedOpenApi.builder()
                .group("08. 공통 (Global)")
                .pathsToMatch("/api/global/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("09. 관리자 (Admin)")
                .pathsToMatch("/api/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi fileApi() {
        return GroupedOpenApi.builder()
                .group("10. 파일 (File)")
                .pathsToMatch("/api/files/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("전체 API")
                .pathsToMatch("/api/**")
                .build();
    }
}
