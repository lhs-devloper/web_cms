package com.lhsdev.cmsproject.controller.global;

import com.lhsdev.cmsproject.repository.SocialServiceConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/global/social")
@RequiredArgsConstructor
@Tag(name = "공통소셜", description = "공개 소셜 로그인 설정 조회 API")
public class SocialGlobalController {

    private final SocialServiceConfigRepository socialConfigRepository;

    @Operation(summary = "활성 소셜 로그인 조회", description = "활성화된 소셜 로그인 제공자 목록을 조회합니다.")
    @GetMapping("/active")
    public ResponseEntity<?> getActiveSocials() {
        return ResponseEntity.ok(
            socialConfigRepository.findByIsActiveTrue().stream()
                .map(config -> Map.of(
                    "registrationId", config.getRegistrationId(),
                    "clientName", config.getClientName()
                ))
                .collect(Collectors.toList())
        );
    }
}
