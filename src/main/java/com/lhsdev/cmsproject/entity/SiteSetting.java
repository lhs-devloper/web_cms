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

    // 5. About (회사소개) Settings
    @Column(length = 255)
    private String aboutHeroTitle = "회사소개";
    @Column(length = 500)
    private String aboutHeroSubtitle = "세상을 연결하는 새로운 디지털 경험,\\nLumière가 만들어갑니다.";
    @Column(length = 255)
    private String aboutVisionTitle = "Our Vision";
    @Column(columnDefinition = "TEXT")
    private String aboutVisionDesc1 = "우리는 단순히 기술을 제공하는 것을 넘어, 사람과 사람, 비즈니스와 가치를 잇는 다리가 되고자 합니다. 한계를 뛰어넘는 혁신적인 플랫폼을 통해 모두가 더 나은 내일을 꿈꿀 수 있도록 만듭니다.";
    @Column(columnDefinition = "TEXT")
    private String aboutVisionDesc2 = "끊임없는 도전과 열정으로 업계의 표준을 새롭게 정의하며, 사용자 중심의 철학을 바탕으로 최상의 디지털 프로덕트를 구현해 나갑니다.";
    @Column(length = 100)
    private String aboutExperienceYears = "10+";
    @Column(length = 100)
    private String aboutGlobalPartners = "200+";
    @Column(length = 255)
    private String aboutSectionOrder = "hero,vision,values";
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean aboutAdvancedMode = false;
    @Column(columnDefinition = "TEXT")
    private String aboutCustomHtml = "";
    @Column(columnDefinition = "TEXT")
    private String aboutCustomCss = "";

    // 6. Location (오시는길) Settings
    @Column(length = 20)
    private String locationMapProvider = "google"; // "google" or "kakao"
    @Column(columnDefinition = "TEXT")
    private String locationKakaoMapIframe = "";

    @Column(length = 255)
    private String locationAddress = "서울특별시 강남구 테헤란로 123 Lumière 타워 15층";
    @Column(length = 255)
    private String locationPhone = "대표전화: 02-1234-5678\\n고객센터: 1588-0000";
    @Column(length = 255)
    private String locationEmail = "제휴/문의: contact@lumiere.com\\n채용: hr@lumiere.com";
    @Column(length = 255)
    private String locationHours = "평일: AM 09:00 - PM 18:00\\n점심: PM 12:30 - PM 13:30";
    @Column(columnDefinition = "TEXT")
    private String locationMapIframe = "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3164.7171442007466!2d127.0305886!3d37.5146059!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x357ca3f707f4ebfb%3A0xeebd12df387a3cb1!2sGangnam-gu%2C%20Seoul!5e0!3m2!1sen!2skr!4v1700000000000!5m2!1sen!2skr";
    @Column(length = 255)
    private String locationSectionOrder = "hero,map,info,transport";
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean locationAdvancedMode = false;
    @Column(columnDefinition = "TEXT")
    private String locationCustomHtml = "";
    @Column(columnDefinition = "TEXT")
    private String locationCustomCss = "";

    // 7. Story (스토리) Settings
    @Column(length = 255)
    private String storyPageTitle = "브랜드 스토리";
    @Column(length = 500)
    private String storyPageSubtitle = "우리의 이야기를 들려드립니다.";
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean storyAdvancedMode = false;
    @Column(columnDefinition = "TEXT")
    private String storyCustomHtml = "";
    @Column(columnDefinition = "TEXT")
    private String storyCustomCss = "";

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
