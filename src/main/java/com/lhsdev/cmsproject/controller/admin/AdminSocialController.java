package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.user.SocialServiceConfig;
import com.lhsdev.cmsproject.service.SocialServiceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/social")
@RequiredArgsConstructor
@Tag(name = "관리자소셜로그인", description = "소셜 로그인 설정 API (관리자 전용)")
public class AdminSocialController {

    @Autowired
    private SocialServiceConfigService socialService;

    @Operation(summary = "소셜 로그인 설정 목록", description = "모든 소셜 로그인 설정을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> list() {
        Map<String, Object> response = new HashMap<>();
        response.put("socials", socialService.findAll());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "소셜 로그인 설정 저장", description = "소셜 로그인 설정을 저장합니다.")
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody SocialServiceConfig config) {
        try {
            socialService.save(config);
            return ResponseEntity.ok(Map.of("success", true, "message",
                    "Social provider saved successfully. Restart application to apply changes."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error saving provider: " + e.getMessage()));
        }
    }

    @Operation(summary = "소셜 로그인 설정 삭제", description = "소셜 로그인 설정을 삭제합니다.")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            socialService.delete(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Provider deleted."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error deleting provider: " + e.getMessage()));
        }
    }
}
