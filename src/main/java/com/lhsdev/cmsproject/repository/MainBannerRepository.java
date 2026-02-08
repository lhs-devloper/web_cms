package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.banner.MainBanner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MainBannerRepository extends JpaRepository<MainBanner, Long> {
    List<MainBanner> findAllByOrderBySortOrderAsc();

    List<MainBanner> findAllByIsActiveTrueOrderBySortOrderAsc();
}
