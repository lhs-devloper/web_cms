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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "메인", description = "메인 페이지 API")
public class MainController {

    @Autowired
    private MainBannerService mainBannerService;

    @Operation(summary = "메인 페이지 데이터", description = "메인 페이지에 필요한 배너 및 사용자 정보를 조회합니다.")
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
