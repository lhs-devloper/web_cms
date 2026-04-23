package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.dto.StoryDto;
import com.lhsdev.cmsproject.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stories")
@RequiredArgsConstructor
@Tag(name = "관리자스토리", description = "스토리 관리 API (관리자 전용)")
public class AdminStoryController {

    private final StoryService storyService;

    @Operation(summary = "전체 스토리 목록", description = "모든 스토리를 조회합니다.")
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(storyService.getAll());
    }

    @Operation(summary = "스토리 생성", description = "새 스토리를 생성합니다.")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody StoryDto dto) {
        try {
            return ResponseEntity.ok(storyService.save(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "스토리 수정", description = "기존 스토리를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody StoryDto dto) {
        try {
            return ResponseEntity.ok(storyService.update(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @Operation(summary = "스토리 삭제", description = "스토리를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            storyService.delete(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
