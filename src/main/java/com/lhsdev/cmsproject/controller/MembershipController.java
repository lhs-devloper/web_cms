package com.lhsdev.cmsproject.controller;

import com.lhsdev.cmsproject.config.auth.PrincipalDetails;
import com.lhsdev.cmsproject.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
@Tag(name = "멤버십", description = "멤버십 등급 및 포인트 조회 API")
public class MembershipController {

    private final MembershipService membershipService;

    @Operation(summary = "활성 등급 목록", description = "활성화된 멤버십 등급 목록을 조회합니다.")
    @GetMapping("/grades")
    public ResponseEntity<?> getGrades() {
        return ResponseEntity.ok(membershipService.getActiveGrades());
    }

    @Operation(summary = "내 멤버십 정보", description = "로그인한 사용자의 포인트 및 등급 정보를 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<?> getMyMembership(@AuthenticationPrincipal PrincipalDetails principal) {
        if (principal == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        var user = principal.getUser();
        return ResponseEntity.ok(Map.of(
            "totalPoints", user.getTotalPoints(),
            "availablePoints", user.getAvailablePoints(),
            "grade", user.getGrade() != null ? Map.of(
                "code", user.getGrade().getCode(),
                "name", user.getGrade().getName(),
                "color", user.getGrade().getColor(),
                "pointRate", user.getGrade().getPointRate(),
                "benefits", user.getGrade().getBenefits() != null ? user.getGrade().getBenefits() : ""
            ) : null
        ));
    }
}
