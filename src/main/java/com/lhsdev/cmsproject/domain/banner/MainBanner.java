package com.lhsdev.cmsproject.domain.banner;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    private String title;
    private String description;
    private String imageUrl;
    private String linkUrl;
    private Integer sortOrder;
    private Boolean isActive;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public MainBanner(String title, String description, String imageUrl, String linkUrl, Integer sortOrder,
            Boolean isActive) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }

    public void update(String title, String description, String imageUrl, String linkUrl, Integer sortOrder,
            Boolean isActive) {
        this.title = title;
        this.description = description;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.imageUrl = imageUrl;
        }
        this.linkUrl = linkUrl;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }
}
