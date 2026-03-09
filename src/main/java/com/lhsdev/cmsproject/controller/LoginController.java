package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.repository.SocialServiceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@lombok.RequiredArgsConstructor
public class LoginController {

    @Autowired
    private SocialServiceConfigRepository socialServiceConfigRepository;

    @GetMapping("/login-config")
    public ResponseEntity<?> loginConfig() {
        java.util.List<String> activeProviders = socialServiceConfigRepository.findByIsActiveTrue().stream()
                .map(config -> config.getRegistrationId().toLowerCase())
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("activeProviders", activeProviders);
        return ResponseEntity.ok(response);
    }
}