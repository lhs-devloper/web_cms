package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.log.VisitorLog;
import com.lhsdev.cmsproject.repository.VisitorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorLogRepository repository;

    @Transactional
    public void logVisit(String ip, String userAgent, String requestUrl) {
        // Here we just save every unique session request that passes the interceptor
        // check.
        // Usually, the interceptor handles the 'unique session check'.
        // This method persists the log.
        VisitorLog log = VisitorLog.builder()
                .ipAddress(ip)
                .userAgent(userAgent != null && userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent)
                .requestUrl(requestUrl)
                .visitDate(LocalDate.now())
                .visitTime(LocalDateTime.now())
                .build();
        repository.save(log);
    }

    @Transactional(readOnly = true)
    public long getTodayVisitorCount() {
        return repository.countByVisitDate(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public long getTotalVisitorCount() {
        return repository.count();
    }

    @Transactional(readOnly = true)
    public java.util.List<com.lhsdev.cmsproject.dto.DailyVisitorStats> getDailyStats(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        return repository.findDailyStats(startDate, endDate);
    }
}
