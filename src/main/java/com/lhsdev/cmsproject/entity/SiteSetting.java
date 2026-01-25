package com.lhsdev.cmsproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cms_site_setting")
@Getter
@Setter
@NoArgsConstructor
public class SiteSetting {

    @Id
    private String id = "DEFAULT"; // Static ID for a single row of settings

    // 1. Meta Tags
    @Column(length = 100)
    private String siteName;
    @Column(length = 255)
    private String metaTitle;
    @Column(columnDefinition = "TEXT")
    private String metaDescription;
    @Column(length = 255)
    private String metaKeywords;

    // 2. Open Graph (SNS Sharing)
    @Column(length = 255)
    private String ogTitle;
    @Column(columnDefinition = "TEXT")
    private String ogDescription;
    @Column(length = 500)
    private String ogImage;
    @Column(length = 100)
    private String ogType = "website";

    // 3. Semantic / Static Site Info (Supports Semantic HTML)
    @Column(length = 255)
    private String logoAltText;
    @Column(length = 500)
    private String logoUrl;
    @Column(length = 255)
    private String footerCopyright;

    // 4. Advanced / Robots / Tracking
    @Column(length = 500)
    private String canonicalUrl;
    @Column(columnDefinition = "TEXT")
    private String robotsTxt;
    @Column(length = 100)
    private String googleAnalyticsId;

    // Factory method for default settings
    public static SiteSetting createDefault() {
        SiteSetting setting = new SiteSetting();
        setting.setSiteName("CMS PRO");
        setting.setMetaTitle("CMS PRO - Modern Solution");
        setting.setMetaDescription("Default description for the CMS.");
        setting.setLogoAltText("CMS PRO Logo");
        setting.setFooterCopyright("© 2024 CMS Project. All rights reserved.");
        setting.setRobotsTxt("User-agent: *\nAllow: /");
        return setting;
    }
}
