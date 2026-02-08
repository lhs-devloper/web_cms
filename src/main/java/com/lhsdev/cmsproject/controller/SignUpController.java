package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.domain.user.AuthProvider;
import com.lhsdev.cmsproject.domain.user.Role;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SignUpController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signupProc")
    public String signupProcess(@RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        // Check duplicate email
        if (userRepository.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "이미 가입된 이메일입니다.");
            return "redirect:/signup";
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .provider(AuthProvider.LOCAL)
                .build();

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요.");
        return "redirect:/login";
    }
}
