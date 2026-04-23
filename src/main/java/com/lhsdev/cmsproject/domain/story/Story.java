package com.lhsdev.cmsproject.domain.story;

import com.lhsdev.cmsproject.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String thumbnailUrl;

    @Builder.Default
    private boolean isPublished = false;

    @Builder.Default
    private int sortOrder = 0;
}
