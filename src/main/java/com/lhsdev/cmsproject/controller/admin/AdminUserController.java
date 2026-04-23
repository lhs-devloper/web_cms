package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.user.Role;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
@Tag(name = "관리자회원관리", description = "회원 관리 API (관리자 전용)")
public class AdminUserController {

    private final UserRepository userRepository;

    @Operation(summary = "회원 목록 조회", description = "키워드 또는 역할로 회원을 검색합니다.")
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

    @Operation(summary = "회원 역할 변경", description = "회원의 역할(권한)을 변경합니다.")
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

    @Operation(summary = "회원 삭제", description = "회원을 삭제합니다.")
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
