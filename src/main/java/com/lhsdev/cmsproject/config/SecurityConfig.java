package com.lhsdev.cmsproject.config;

import com.lhsdev.cmsproject.config.auth.CustomOAuth2UserService;
import com.lhsdev.cmsproject.config.auth.CustomOAuth2SuccessHandler;
import com.lhsdev.cmsproject.config.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomOAuth2UserService customOAuth2UserService;
        private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/uploads/**",
                                                                "/h2-console/**", "/profile", "/admin/**", "/board/**",
                                                                "/api/files/**", "/signup", "/signupProc",
                                                                "/api/admin/**", "/api/auth/**", "/api/board/**",
                                                                "/api/global/**", "/api/uploads/**",
                                                                "/v3/api-docs/**", "/v3/api-docs.yaml",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html", "/swagger-resources/**",
                                                                "/webjars/**", "/error")
                                                .permitAll()
                                                .requestMatchers("/api/v1/**", "/api/profile/**").hasRole("USER")
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/loginProc")
                                                // .defaultSuccessUrl("/") // SSR(Thymeleaf용) 방식 대신 프론트엔드 연동용으로 제거 또는
                                                // API응답 등 수정 필요. (현재는 보류)
                                                .permitAll())
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/login")
                                                .successHandler(customOAuth2SuccessHandler) // 핵심: 커스텀 성공 핸들러 장착
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(customOAuth2UserService)))
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/") // TODO: 프론트 쪽 로그아웃 콜백으로 변경
                                                .invalidateHttpSession(true));

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // 프론트엔드 전체 주소 (외부 기기 포함) 허용
                configuration.setAllowedOriginPatterns(Arrays.asList("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true); // 쿠키 포함 여부
                configuration.setMaxAge(3600L); // preflight 요청 캐시 시간

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
