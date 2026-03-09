package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.dto.DailyVisitorStats;
import com.lhsdev.cmsproject.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final VisitorService visitorService;

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
