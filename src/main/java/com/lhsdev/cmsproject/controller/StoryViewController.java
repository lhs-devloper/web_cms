package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryViewController {

    private final StoryService storyService;

    @GetMapping
    public ResponseEntity<?> getPublished() {
        return ResponseEntity.ok(storyService.getPublished());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(storyService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
