package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.domain.banner.MainBanner;
import com.lhsdev.cmsproject.repository.MainBannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/global/banners")
@RequiredArgsConstructor
@Tag(name = "공통배너", description = "공개 배너 조회 API")
public class PublicBannerController {

    private final MainBannerRepository bannerRepository;

    @Operation(summary = "활성 배너 조회", description = "현재 활성화된 배너 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MainBanner>> getActiveBanners() {
        return ResponseEntity.ok(bannerRepository.findAllByIsActiveTrueOrderBySortOrderAsc());
    }
}
