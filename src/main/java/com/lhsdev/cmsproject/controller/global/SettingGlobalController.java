package com.lhsdev.cmsproject.controller.global;

import com.lhsdev.cmsproject.entity.SiteSetting;
import com.lhsdev.cmsproject.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/global/setting")
@RequiredArgsConstructor
@Tag(name = "공통사이트설정", description = "공개 사이트 설정 조회 API")
public class SettingGlobalController {

    private final SettingService settingService;

    @Operation(summary = "사이트 설정 조회", description = "공개 사이트 설정 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<SiteSetting> getSetting() {
        return ResponseEntity.ok(settingService.getSetting());
    }
}
