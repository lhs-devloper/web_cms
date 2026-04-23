package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.payment.PaymentConfig;
import com.lhsdev.cmsproject.service.PaymentConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/payment")
@RequiredArgsConstructor
@Tag(name = "관리자결제설정", description = "결제 설정 관리 API (관리자 전용)")
public class AdminPaymentController {

    @Autowired
    private PaymentConfigService paymentConfigService;

    @Operation(summary = "결제 설정 목록", description = "모든 PG사 결제 설정을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(Map.of("payments", paymentConfigService.findAll()));
    }

    @Operation(summary = "결제 설정 조회", description = "특정 PG사의 결제 설정을 조회합니다.")
    @GetMapping("/{pg}")
    public ResponseEntity<?> get(@PathVariable String pg) {
        String provider = resolveProvider(pg);
        return paymentConfigService.findByProvider(provider)
                .map(config -> ResponseEntity.ok((Object) config))
                .orElse(ResponseEntity.badRequest().body(Map.of("message", "해당 결제 설정을 찾을 수 없습니다.")));
    }

    @Operation(summary = "결제 설정 저장", description = "PG사 결제 설정을 저장합니다.")
    @PostMapping("/{pg}/save")
    public ResponseEntity<?> save(@PathVariable String pg, @RequestBody Map<String, Object> request) {
        try {
            String provider = resolveProvider(pg);
            PaymentConfig config = paymentConfigService.findByProvider(provider)
                    .orElseThrow(() -> new IllegalArgumentException("해당 결제 설정을 찾을 수 없습니다: " + pg));

            config.update(
                    (String) request.getOrDefault("clientKey", config.getClientKey()),
                    (String) request.getOrDefault("secretKey", config.getSecretKey()),
                    (String) request.getOrDefault("cid", config.getCid()),
                    (String) request.getOrDefault("apiUrl", config.getApiUrl()),
                    (String) request.getOrDefault("displayName", config.getDisplayName()),
                    (boolean) request.getOrDefault("isActive", config.isActive())
            );

            paymentConfigService.save(config);
            return ResponseEntity.ok(Map.of("success", true, "message", config.getDisplayName() + " 설정이 저장되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "결제 설정 저장 실패: " + e.getMessage()));
        }
    }

    private String resolveProvider(String pg) {
        return switch (pg.toLowerCase()) {
            case "kcp" -> "KCP";
            case "kakao", "kakaopay" -> "KAKAOPAY";
            case "toss", "tosspay" -> "TOSSPAY";
            default -> pg.toUpperCase();
        };
    }
}
