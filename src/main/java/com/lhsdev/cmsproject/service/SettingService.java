package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.entity.SiteSetting;
import com.lhsdev.cmsproject.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingService {
    @Autowired
    private SiteSettingRepository siteSettingRepository;

    @Transactional(readOnly = true)
    public SiteSetting getSetting() {
        return siteSettingRepository.findById("DEFAULT")
                .orElseGet(() -> SiteSetting.createDefault());
    }

    @Transactional
    public void saveSetting(SiteSetting setting) {
        setting.setId("DEFAULT"); // Ensure it always overwrites the same row
        siteSettingRepository.save(setting);
    }
}
