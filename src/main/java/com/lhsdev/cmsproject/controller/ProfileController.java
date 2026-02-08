package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import com.lhsdev.cmsproject.domain.user.User;
import com.lhsdev.cmsproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${app.upload.dir}")
    private String uploadDir;

    @GetMapping
    public String profile(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepository.findById(principal.getUser().getId()).orElseThrow();
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam String name,
            @RequestParam(required = false) org.springframework.web.multipart.MultipartFile file,
            RedirectAttributes redirectAttributes) {
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

                picturePath = "/uploads/" + fileName;
            } catch (java.io.IOException e) {
                redirectAttributes.addFlashAttribute("error", "프로필 이미지 업로드 실패: " + e.getMessage());
                return "redirect:/profile";
            }
        }

        user.update(name, picturePath);
        userRepository.save(user);

        // Update session principal
        principal.setUser(user);

        redirectAttributes.addFlashAttribute("message", "프로필이 업데이트되었습니다.");
        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String updatePassword(@AuthenticationPrincipal PrincipalDetails principal,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(principal.getUser().getId()).orElseThrow();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다.");

        return "redirect:/profile";
    }
}
