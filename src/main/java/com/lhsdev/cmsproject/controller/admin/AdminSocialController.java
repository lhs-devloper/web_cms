package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.user.SocialServiceConfig;
import com.lhsdev.cmsproject.service.SocialServiceConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/social")
@RequiredArgsConstructor
public class AdminSocialController {

    @Autowired
    private SocialServiceConfigService socialService;

    @GetMapping
    public ResponseEntity<?> list() {
        Map<String, Object> response = new HashMap<>();
        response.put("socials", socialService.findAll());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@ModelAttribute SocialServiceConfig config) {
        try {
            socialService.save(config);
            return ResponseEntity.ok(Map.of("success", true, "message",
                    "Social provider saved successfully. Restart application to apply changes."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error saving provider: " + e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam Long id) {
        try {
            socialService.delete(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Provider deleted."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error deleting provider: " + e.getMessage()));
        }
    }
}
