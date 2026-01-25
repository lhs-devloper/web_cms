package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.entity.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteSettingRepository extends JpaRepository<SiteSetting, String> {
}
