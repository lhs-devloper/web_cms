package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.user.Role;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> list(
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

        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        response.put("roles", Role.values());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-role")
    public ResponseEntity<?> updateRole(@RequestParam Long id, @RequestParam Role role) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

            user.updateRole(role);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("success", true, "message", "User role updated successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error updating role: " + e.getMessage()));
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "User deleted successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error deleting user: " + e.getMessage()));
        }
    }
}
