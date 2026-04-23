package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import com.lhsdev.cmsproject.config.auth.jwt.JwtProvider;
import com.lhsdev.cmsproject.domain.user.Role;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "로그인, 회원가입 API")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "이메일 또는 비밀번호가 잘못되었습니다."));
        }

        String token = jwtProvider.createToken(user.getEmail(), user.getRoleKey());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRoleKey()));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 가입된 이메일입니다."));
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(Role.USER)
                // Default provider config for local users
                .provider(com.lhsdev.cmsproject.domain.user.AuthProvider.LOCAL)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
    }
}

@Data
class LoginRequest {
    private String email;
    private String password;
}

@Data
class SignupRequest {
    private String name;
    private String email;
    private String password;
}
