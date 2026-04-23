package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.domain.user.AuthProvider;
import com.lhsdev.cmsproject.domain.user.Role;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
@Tag(name = "초기설정", description = "최초 관리자 계정 설정 API")
public class SetupController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "관리자 존재 확인", description = "관리자 계정이 존재하는지 확인합니다.")
    @GetMapping("/check")
    public ResponseEntity<?> checkAdmin() {
        boolean hasAdmin = !userRepository.findByRole(Role.SUPER_ADMIN).isEmpty()
                || !userRepository.findByRole(Role.ADMIN).isEmpty();
        return ResponseEntity.ok(Map.of("hasAdmin", hasAdmin));
    }

    @Operation(summary = "초기 관리자 생성", description = "최초 관리자 계정을 생성합니다. 이미 관리자가 존재하면 거부됩니다.")
    @PostMapping("/init")
    public ResponseEntity<?> createAdmin(@RequestBody AdminSetupRequest request) {
        if (!userRepository.findByRole(Role.SUPER_ADMIN).isEmpty() || !userRepository.findByRole(Role.ADMIN).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "관리자 계정이 이미 존재합니다."));
        }

        if (request.getEmail() == null || request.getEmail().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank() ||
            request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "모든 필드를 입력해주세요."));
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 사용 중인 이메일입니다."));
        }

        User admin = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.SUPER_ADMIN)
                .provider(AuthProvider.LOCAL)
                .build();

        userRepository.save(admin);
        return ResponseEntity.ok(Map.of("success", true, "message", "관리자 계정이 생성되었습니다."));
    }
}

@Data
class AdminSetupRequest {
    private String name;
    private String email;
    private String password;
}
