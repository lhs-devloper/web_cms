package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.user.Role;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;

    @GetMapping
    public String list(Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Role role) {

        java.util.List<User> users;

        if (keyword != null && !keyword.trim().isEmpty() && role != null) {
            users = userRepository.findByNameContainingAndRole(keyword, role);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            users = userRepository.findByNameContaining(keyword);
        } else if (role != null) {
            users = userRepository.findByRole(role);
        } else {
            users = userRepository.findAll();
        }

        model.addAttribute("users", users);
        model.addAttribute("activeLink", "user");
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);
        model.addAttribute("roles", Role.values());
        return "admin/user";
    }

    @PostMapping("/update-role")
    public String updateRole(@RequestParam Long id, @RequestParam Role role, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
            // Prevent self-demotion if needed, but for simplicity let's allow it (or warn).
            // Usually we check if the user is modifying themself.

            user.updateRole(role);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("message", "User role updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating role: " + e.getMessage());
        }
        return "redirect:/admin/user";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/user";
    }
}
