package com.lhsdev.cmsproject.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String requestUri = request.getRequestURI();

        if (requestUri.startsWith("/api/")) {
            // API 요청: 401 JSON 응답
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    objectMapper.writeValueAsString(Map.of("message", "로그인이 필요합니다."))
            );
        } else {
            // 페이지 요청: /login으로 리다이렉트 (기존 동작 유지)
            response.sendRedirect("/login");
        }
    }
}
