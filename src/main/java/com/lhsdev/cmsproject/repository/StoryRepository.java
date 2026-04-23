package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.story.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findAllByOrderBySortOrderAsc();
    List<Story> findByIsPublishedTrueOrderBySortOrderAsc();
}
