package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import com.lhsdev.cmsproject.service.MainBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainController {

    @Autowired
    private MainBannerService mainBannerService;

    @GetMapping("/")
    public ResponseEntity<?> main(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, Object> response = new HashMap<>();
        if (principalDetails != null) {
            response.put("user", principalDetails.getUser());
        }
        java.util.List<com.lhsdev.cmsproject.domain.banner.MainBanner> banners = mainBannerService.getActiveBanners();
        response.put("banners", banners);
        return ResponseEntity.ok(response);
    }
}
