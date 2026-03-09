package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.domain.banner.MainBanner;
import com.lhsdev.cmsproject.repository.MainBannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/global/banners")
@RequiredArgsConstructor
public class PublicBannerController {

    private final MainBannerRepository bannerRepository;

    @GetMapping
    public ResponseEntity<List<MainBanner>> getActiveBanners() {
        return ResponseEntity.ok(bannerRepository.findAllByIsActiveTrueOrderBySortOrderAsc());
    }
}
