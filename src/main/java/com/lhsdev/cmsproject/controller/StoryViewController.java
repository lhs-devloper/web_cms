package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@Tag(name = "스토리", description = "브랜드 스토리 조회 API")
public class StoryViewController {

    private final StoryService storyService;

    @Operation(summary = "공개 스토리 목록", description = "공개된 스토리 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getPublished() {
        return ResponseEntity.ok(storyService.getPublished());
    }

    @Operation(summary = "스토리 상세 조회", description = "스토리 ID로 상세 내용을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(storyService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
