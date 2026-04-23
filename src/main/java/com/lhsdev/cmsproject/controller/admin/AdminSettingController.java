package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.entity.SiteSetting;
import com.lhsdev.cmsproject.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/setting")
@RequiredArgsConstructor
@Tag(name = "관리자사이트설정", description = "사이트 설정 관리 API (관리자 전용)")
public class AdminSettingController {

    private final SettingService settingService;

    @Operation(summary = "사이트 설정 조회", description = "현재 사이트 설정을 조회합니다.")
    @GetMapping
    public ResponseEntity<SiteSetting> getSetting() {
        return ResponseEntity.ok(settingService.getSetting());
    }

    @Operation(summary = "사이트 설정 저장", description = "사이트 설정을 저장합니다.")
    @PostMapping("/save")
    public ResponseEntity<?> saveSetting(@RequestBody SiteSetting setting) {
        try {
            settingService.saveSetting(setting);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving settings: " + e.getMessage());
        }
    }
}
