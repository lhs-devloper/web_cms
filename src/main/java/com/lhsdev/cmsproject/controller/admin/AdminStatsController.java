package com.lhsdev.cmsproject.controller.admin;

import com.lhsdev.cmsproject.dto.DailyVisitorStats;
import com.lhsdev.cmsproject.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final VisitorService visitorService;

    @GetMapping
    public String stats(Model model) {
        // Fetch last 30 days
        List<DailyVisitorStats> stats = visitorService.getDailyStats(30);

        // Transform for Chart.js
        List<String> labels = stats.stream()
                .map(s -> "\"" + s.getDate().toString() + "\"")
                .collect(Collectors.toList());
        List<Long> data = stats.stream()
                .map(DailyVisitorStats::getCount)
                .collect(Collectors.toList());

        model.addAttribute("visitorLabels", labels);
        model.addAttribute("visitorData", data);
        model.addAttribute("todayCount", visitorService.getTodayVisitorCount());
        model.addAttribute("totalCount", visitorService.getTotalVisitorCount());

        // Active link sidebar
        model.addAttribute("activeLink", "stats");

        return "admin/stats";
    }
}
