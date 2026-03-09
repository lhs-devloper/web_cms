package com.lhsdev.cmsproject.domain.banner;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;
import java.time.LocalDateTime;

@Entity
@Table(name = "main_banners")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MainBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Content
    private String subtitle;
    private String title;
    private String description;
    private String imageUrl; // Can be image or video URL
    private String linkUrl;

    // Style & Positioning Controls
    private String titleFontSize = "5.5rem";
    private String titleColor = "#ffffff";
    private String subtitleColor = "#00d2ff"; // Default to primary-color
    private String textAlignment = "left"; // "left", "center", "right"

    // Buttons (JSON Format)
    @Column(columnDefinition = "TEXT")
    private String buttonsJson = "[{\"text\":\"SHOP NOW\", \"linkUrl\":\"#\", \"bgColor\":\"#ffffff\", \"textColor\":\"#000000\"}]";

    // Metadata
    private Integer sortOrder = 0;
    private Boolean isActive = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public MainBanner(String subtitle, String title, String description, String imageUrl, String linkUrl,
            String titleFontSize, String titleColor, String subtitleColor, String textAlignment,
            String buttonsJson, Integer sortOrder, Boolean isActive) {
        this.subtitle = subtitle;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.titleFontSize = titleFontSize;
        this.titleColor = titleColor;
        this.subtitleColor = subtitleColor;
        this.textAlignment = textAlignment;
        this.buttonsJson = buttonsJson != null ? buttonsJson : "[]";
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }

    public void update(String subtitle, String title, String description, String imageUrl, String linkUrl,
            String titleFontSize, String titleColor, String subtitleColor, String textAlignment,
            String buttonsJson, Integer sortOrder, Boolean isActive) {
        this.subtitle = subtitle;
        this.title = title;
        this.description = description;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.imageUrl = imageUrl;
        }
        this.linkUrl = linkUrl;
        this.titleFontSize = titleFontSize;
        this.titleColor = titleColor;
        this.subtitleColor = subtitleColor;
        this.textAlignment = textAlignment;
        this.buttonsJson = buttonsJson != null ? buttonsJson : "[]";
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }
}
