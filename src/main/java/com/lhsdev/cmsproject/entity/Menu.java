package com.lhsdev.cmsproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cms_menus")
@Getter
@Setter
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "main_1depth", nullable = false)
    private String main1Depth;

    @Column(name = "main_2depth")
    private String main2Depth;

    @Column(nullable = false)
    private String link;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Menu(String main1Depth, String main2Depth, String link) {
        this.main1Depth = main1Depth;
        this.main2Depth = main2Depth;
        this.link = link;
    }
}
