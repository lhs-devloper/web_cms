package com.lhsdev.cmsproject.api;

import com.lhsdev.cmsproject.domain.user.Role;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserApiController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Role role) {

        List<User> users;

        if (keyword != null && !keyword.trim().isEmpty() && role != null) {
            users = userRepository.findByNameContainingAndRole(keyword, role);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            users = userRepository.findByNameContaining(keyword);
        } else if (role != null) {
            users = userRepository.findByRole(role);
        } else {
            users = userRepository.findAll();
        }

        // Convert to DTO to avoid JSON infinite recursion or exposing sensitive data
        List<Map<String, Object>> result = users.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("name", user.getName());
            map.put("email", user.getEmail());
            map.put("provider", user.getProvider());
            map.put("role", user.getRole());
            map.put("picture", user.getPicture());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
