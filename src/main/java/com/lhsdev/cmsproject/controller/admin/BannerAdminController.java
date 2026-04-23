package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.banner.MainBanner;
import com.lhsdev.cmsproject.service.MainBannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
@Tag(name = "관리자배너", description = "메인 배너 관리 API (관리자 전용)")
public class BannerAdminController {

    private final MainBannerService mainBannerService;

    @Operation(summary = "배너 목록 조회", description = "모든 배너를 조회합니다.")
    @GetMapping
    public ResponseEntity<?> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("banners", mainBannerService.getAllBanners());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배너 폼 조회", description = "배너 등록/수정을 위한 폼 데이터를 조회합니다.")
    @GetMapping("/form")
    public ResponseEntity<?> form(@RequestParam(required = false) Long id) {
        Map<String, Object> response = new HashMap<>();
        if (id != null) {
            response.put("banner", mainBannerService.getBanner(id));
        } else {
            response.put("banner", MainBanner.builder().build());
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배너 저장", description = "배너를 등록 또는 수정합니다.")
    @PostMapping("/save")
    public ResponseEntity<?> save(@ModelAttribute MainBanner banner,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            if (banner.getId() != null) {
                mainBannerService.updateBanner(banner.getId(), banner, file);
            } else {
                mainBannerService.saveBanner(banner, file);
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Banner saved successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error saving banner: " + e.getMessage()));
        }
    }

    @Operation(summary = "배너 삭제", description = "배너를 삭제합니다.")
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            mainBannerService.deleteBanner(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Banner deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error deleting banner: " + e.getMessage()));
        }
    }
}
