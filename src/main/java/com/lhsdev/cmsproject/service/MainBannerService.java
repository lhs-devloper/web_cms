package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.banner.MainBanner;
import com.lhsdev.cmsproject.repository.MainBannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MainBannerService {

    private final MainBannerRepository mainBannerRepository;

    @org.springframework.beans.factory.annotation.Value("${app.upload.dir}")
    private String uploadDir;

    public List<MainBanner> getAllBanners() {
        return mainBannerRepository.findAllByOrderBySortOrderAsc();
    }

    public List<MainBanner> getActiveBanners() {
        return mainBannerRepository.findAllByIsActiveTrueOrderBySortOrderAsc();
    }

    @Transactional
    public void saveBanner(MainBanner banner, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String imageUrl = uploadFile(file);
            banner.setImageUrl(imageUrl);
        }
        mainBannerRepository.save(banner);
    }

    @Transactional
    public void updateBanner(Long id, MainBanner requestBanner, MultipartFile file) throws IOException {
        MainBanner banner = mainBannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid banner Id:" + id));

        if (file != null && !file.isEmpty()) {
            String imageUrl = uploadFile(file);
            banner.setImageUrl(imageUrl);
        }

        banner.setTitle(requestBanner.getTitle());
        banner.setDescription(requestBanner.getDescription());
        banner.setLinkUrl(requestBanner.getLinkUrl());
        banner.setSortOrder(requestBanner.getSortOrder());
        banner.setIsActive(requestBanner.getIsActive());

        // No explicit save needed in Transactional, but harmless
    }

    @Transactional
    public void deleteBanner(Long id) {
        mainBannerRepository.deleteById(id);
    }

    private String uploadFile(MultipartFile file) throws IOException {
        // Use configured uploadDir. Ensure it ends with / before appending subfolder
        String basePath = uploadDir;
        if (!basePath.endsWith("/") && !basePath.endsWith("\\")) {
            basePath += "/";
        }

        Path uploadPath = Paths.get(basePath + "banners");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(storedFilename);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("File saved to: " + filePath.toAbsolutePath());
        return "/uploads/banners/" + storedFilename;
    }

    public MainBanner getBanner(Long id) {
        return mainBannerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid banner Id:" + id));
    }
}
