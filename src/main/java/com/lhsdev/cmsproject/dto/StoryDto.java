package com.lhsdev.cmsproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoryDto {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String thumbnailUrl;
    private boolean isPublished;
    private int sortOrder;
}
