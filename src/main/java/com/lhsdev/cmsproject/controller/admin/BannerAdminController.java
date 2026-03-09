package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.banner.MainBanner;
import com.lhsdev.cmsproject.service.MainBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
public class BannerAdminController {

    private final MainBannerService mainBannerService;

    @GetMapping
    public ResponseEntity<?> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("banners", mainBannerService.getAllBanners());
        return ResponseEntity.ok(response);
    }

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
