package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.dto.DailyVisitorStats;
import com.lhsdev.cmsproject.repository.UserRepository;
import com.lhsdev.cmsproject.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Tag(name = "관리자통계", description = "방문자 통계 API (관리자 전용)")
public class AdminStatsController {

    private final VisitorService visitorService;

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "대시보드 요약 통계", description = "총 회원수, 오늘/전체 방문자 수를 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalMembers", userRepository.count());
        summary.put("todayVisitors", visitorService.getTodayVisitorCount());
        summary.put("totalVisitors", visitorService.getTotalVisitorCount());
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "활동 로그 조회", description = "방문자 활동 로그를 페이징/필터로 조회합니다.")
    @GetMapping("/activity-log")
    public ResponseEntity<?> getActivityLog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String url) {
        return ResponseEntity.ok(visitorService.getActivityLog(page, size, startDate, endDate, ip, url));
    }

    @Operation(summary = "방문자 통계 조회", description = "일별 방문자 통계를 조회합니다.")
    @GetMapping
    public ResponseEntity<?> stats() {
        List<DailyVisitorStats> stats = visitorService.getDailyStats(30);

        List<String> labels = stats.stream()
                .map(s -> "\"" + s.getDate().toString() + "\"")
                .collect(Collectors.toList());
        List<Long> data = stats.stream()
                .map(DailyVisitorStats::getCount)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("visitorLabels", labels);
        response.put("visitorData", data);
        response.put("todayCount", visitorService.getTodayVisitorCount());
        response.put("totalCount", visitorService.getTotalVisitorCount());

        return ResponseEntity.ok(response);
    }
}
