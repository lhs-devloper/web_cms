package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.user.SocialServiceConfig;
import com.lhsdev.cmsproject.service.SocialServiceConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/social")
@RequiredArgsConstructor
public class AdminSocialController {

    private final SocialServiceConfigService socialService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("socials", socialService.findAll());
        model.addAttribute("activeLink", "social");
        return "admin/social";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute SocialServiceConfig config, RedirectAttributes redirectAttributes) {
        try {
            // Set defaults if empty
            if (config.getAuthorizationGrantType() == null || config.getAuthorizationGrantType().isEmpty()) {
                // We can't set it here easily since it's a field in the entity, but let's
                // assume validation passes or HTML form sends it.
                // Or handle updates logically. Use service.
            }
            socialService.save(config);
            redirectAttributes.addFlashAttribute("message",
                    "Social provider saved successfully. Restart application to apply changes.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving provider: " + e.getMessage());
        }
        return "redirect:/admin/social";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            socialService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Provider deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting provider: " + e.getMessage());
        }
        return "redirect:/admin/social";
    }
}
