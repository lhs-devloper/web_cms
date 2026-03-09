package com.lhsdev.cmsproject.controller.global;

import com.lhsdev.cmsproject.entity.SiteSetting;
import com.lhsdev.cmsproject.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/global/setting")
@RequiredArgsConstructor
public class SettingGlobalController {

    private final SettingService settingService;

    @GetMapping
    public ResponseEntity<SiteSetting> getSetting() {
        return ResponseEntity.ok(settingService.getSetting());
    }
}
