package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.repository.SocialServiceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@lombok.RequiredArgsConstructor
public class LoginController {

    @Autowired
    private SocialServiceConfigRepository socialServiceConfigRepository;

    @GetMapping("/login")
    public String login(org.springframework.ui.Model model) {
        java.util.List<String> activeProviders = socialServiceConfigRepository.findByIsActiveTrue().stream()
                .map(config -> config.getRegistrationId().toLowerCase())
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("activeProviders", activeProviders);
        return "login";
    }
}