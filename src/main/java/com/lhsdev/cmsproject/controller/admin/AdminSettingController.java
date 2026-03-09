package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.entity.SiteSetting;
import com.lhsdev.cmsproject.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/setting")
@RequiredArgsConstructor
public class AdminSettingController {

    private final SettingService settingService;

    @GetMapping
    public ResponseEntity<SiteSetting> getSetting() {
        return ResponseEntity.ok(settingService.getSetting());
    }

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
