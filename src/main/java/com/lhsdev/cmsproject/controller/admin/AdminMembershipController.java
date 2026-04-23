package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.domain.membership.MembershipGrade;
import com.lhsdev.cmsproject.repository.MembershipGradeRepository;
import com.lhsdev.cmsproject.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/membership")
@RequiredArgsConstructor
@Tag(name = "관리자멤버십", description = "멤버십 등급 관리 API (관리자 전용)")
public class AdminMembershipController {

    private final MembershipGradeRepository gradeRepository;
    private final MembershipService membershipService;

    @Operation(summary = "전체 등급 목록", description = "모든 멤버십 등급을 조회합니다.")
    @GetMapping("/grades")
    public ResponseEntity<?> getAllGrades() {
        return ResponseEntity.ok(membershipService.getAllGrades());
    }

    @Operation(summary = "등급 생성", description = "새 멤버십 등급을 생성합니다.")
    @PostMapping("/grades")
    public ResponseEntity<?> createGrade(@RequestBody MembershipGrade grade) {
        return ResponseEntity.ok(gradeRepository.save(grade));
    }

    @Operation(summary = "등급 수정", description = "멤버십 등급 정보를 수정합니다.")
    @PutMapping("/grades/{id}")
    public ResponseEntity<?> updateGrade(@PathVariable Long id, @RequestBody MembershipGrade dto) {
        MembershipGrade grade = gradeRepository.findById(id).orElseThrow();
        grade.setCode(dto.getCode());
        grade.setName(dto.getName());
        grade.setDescription(dto.getDescription());
        grade.setMinPoints(dto.getMinPoints());
        grade.setPointRate(dto.getPointRate());
        grade.setBenefits(dto.getBenefits());
        grade.setColor(dto.getColor());
        grade.setSortOrder(dto.getSortOrder());
        grade.setActive(dto.isActive());
        return ResponseEntity.ok(gradeRepository.save(grade));
    }

    @Operation(summary = "등급 삭제", description = "멤버십 등급을 삭제합니다.")
    @DeleteMapping("/grades/{id}")
    public ResponseEntity<?> deleteGrade(@PathVariable Long id) {
        gradeRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @Operation(summary = "회원 포인트 조정", description = "특정 회원의 포인트를 수동으로 조정합니다.")
    @PatchMapping("/users/{userId}/points")
    public ResponseEntity<?> adjustPoints(@PathVariable Long userId, @RequestBody Map<String, Integer> request) {
        try {
            membershipService.adjustPoints(userId, request.get("totalPoints"), request.get("availablePoints"));
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
