package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.domain.log.VisitorLog;
import com.lhsdev.cmsproject.repository.VisitorLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<VisitorLog> getActivityLog(int page, int size, String startDate, String endDate, String ip, String url) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "visitDate", "visitTime"));

        LocalDate start = startDate != null && !startDate.isEmpty() ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null && !endDate.isEmpty() ? LocalDate.parse(endDate) : null;

        if (start != null && end != null) {
            if (ip != null && !ip.isEmpty()) {
                return repository.findByVisitDateBetweenAndIpAddressContaining(start, end, ip, pageable);
            }
            return repository.findByVisitDateBetween(start, end, pageable);
        }
        if (ip != null && !ip.isEmpty()) {
            return repository.findByIpAddressContaining(ip, pageable);
        }
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.lhsdev.cmsproject.dto.DailyVisitorStats> getDailyStats(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        return repository.findDailyStats(startDate, endDate);
    }
}
