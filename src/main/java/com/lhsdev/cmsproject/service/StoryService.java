package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.story.Story;
import com.lhsdev.cmsproject.dto.StoryDto;
import com.lhsdev.cmsproject.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;

    @Transactional(readOnly = true)
    public List<Story> getAll() {
        return storyRepository.findAllByOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public List<Story> getPublished() {
        return storyRepository.findByIsPublishedTrueOrderBySortOrderAsc();
    }

    @Transactional(readOnly = true)
    public Story getById(Long id) {
        return storyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스토리를 찾을 수 없습니다."));
    }

    @Transactional
    public Story save(StoryDto dto) {
        Story story = Story.builder()
                .title(dto.getTitle())
                .summary(dto.getSummary())
                .content(dto.getContent())
                .thumbnailUrl(dto.getThumbnailUrl())
                .isPublished(dto.isPublished())
                .sortOrder(dto.getSortOrder())
                .build();
        return storyRepository.save(story);
    }

    @Transactional
    public Story update(Long id, StoryDto dto) {
        Story story = getById(id);
        story.setTitle(dto.getTitle());
        story.setSummary(dto.getSummary());
        story.setContent(dto.getContent());
        story.setThumbnailUrl(dto.getThumbnailUrl());
        story.setPublished(dto.isPublished());
        story.setSortOrder(dto.getSortOrder());
        return story;
    }

    @Transactional
    public void delete(Long id) {
        storyRepository.deleteById(id);
    }
}
