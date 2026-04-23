package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.dto.StoryDto;
import com.lhsdev.cmsproject.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stories")
@RequiredArgsConstructor
public class AdminStoryController {

    private final StoryService storyService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(storyService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody StoryDto dto) {
        try {
            return ResponseEntity.ok(storyService.save(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody StoryDto dto) {
        try {
            return ResponseEntity.ok(storyService.update(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

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
