package com.lhsdev.cmsproject.domain.menu;

import com.lhsdev.cmsproject.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cms_menu")
@Getter
@NoArgsConstructor
public class MainMenu extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String linkUrl;
    private Integer sortOrder;
    private Boolean isActive;

    @jakarta.persistence.Column(name = "parent_id")
    private Long parentId;

    @Builder
    public MainMenu(String title, String linkUrl, Integer sortOrder, Boolean isActive, Long parentId) {
        this.title = title;
        this.linkUrl = linkUrl;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
        this.parentId = parentId;
    }

    public void update(String title, String linkUrl, Integer sortOrder, Boolean isActive, Long parentId) {
        this.title = title;
        this.linkUrl = linkUrl;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
        this.parentId = parentId;
    }
}
