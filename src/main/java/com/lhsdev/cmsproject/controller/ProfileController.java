package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "프로필", description = "사용자 프로필 관리 API")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.upload.dir}")
    private String uploadDir;

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필을 조회합니다.")
    @GetMapping
    public ResponseEntity<?> profile(@AuthenticationPrincipal PrincipalDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findById(principal.getUser().getId()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 수정", description = "사용자 이름 및 프로필 사진을 수정합니다.")
    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile file) {

        if (principal == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userRepository.findById(principal.getUser().getId()).orElseThrow();

        String picturePath = user.getPicture();

        if (file != null && !file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                String ext = originalFilename != null && originalFilename.contains(".")
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : ".jpg";
                String uuid = java.util.UUID.randomUUID().toString();
                String fileName = uuid + ext;

                java.nio.file.Path path = java.nio.file.Paths.get(uploadDir, fileName);
                java.nio.file.Files.copy(file.getInputStream(), path,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                picturePath = "/api/uploads/" + fileName; // Maybe serve static files or redirect
            } catch (java.io.IOException e) {
                return ResponseEntity.badRequest().body(Map.of("message", "프로필 이미지 업로드 실패: " + e.getMessage()));
            }
        }

        user.update(name, picturePath);
        userRepository.save(user);
        principal.setUser(user);

        return ResponseEntity.ok(Map.of("success", true, "message", "프로필이 업데이트되었습니다."));
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경합니다.")
    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(@AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {

        if (principal == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userRepository.findById(principal.getUser().getId()).orElseThrow();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "현재 비밀번호가 일치하지 않습니다."));
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("message", "새 비밀번호가 일치하지 않습니다."));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("success", true, "message", "비밀번호가 변경되었습니다."));
    }
}
