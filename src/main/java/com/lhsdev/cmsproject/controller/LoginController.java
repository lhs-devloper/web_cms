package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.repository.SocialServiceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@lombok.RequiredArgsConstructor
@Tag(name = "로그인 설정", description = "로그인 페이지 설정 API")
public class LoginController {

    @Autowired
    private SocialServiceConfigRepository socialServiceConfigRepository;

    @Operation(summary = "로그인 설정 조회", description = "활성화된 소셜 로그인 설정을 조회합니다.")
    @GetMapping("/login-config")
    public ResponseEntity<?> loginConfig() {
        java.util.List<String> activeProviders = socialServiceConfigRepository.findByIsActiveTrue().stream()
                .map(config -> config.getRegistrationId().toLowerCase())
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("activeProviders", activeProviders);
        return ResponseEntity.ok(response);
    }
}