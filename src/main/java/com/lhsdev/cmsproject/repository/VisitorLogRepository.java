package com.lhsdev.cmsproject.repository;

import com.lhsdev.cmsproject.domain.log.VisitorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {
    long countByVisitDate(LocalDate visitDate);

    @org.springframework.data.jpa.repository.Query("SELECT v.visitDate as date, COUNT(v) as count FROM VisitorLog v WHERE v.visitDate BETWEEN :startDate AND :endDate GROUP BY v.visitDate ORDER BY v.visitDate ASC")
    java.util.List<com.lhsdev.cmsproject.dto.DailyVisitorStats> findDailyStats(LocalDate startDate, LocalDate endDate);

    Page<VisitorLog> findByVisitDateBetween(LocalDate start, LocalDate end, Pageable pageable);
    Page<VisitorLog> findByVisitDateBetweenAndIpAddressContaining(LocalDate start, LocalDate end, String ip, Pageable pageable);
    Page<VisitorLog> findByIpAddressContaining(String ip, Pageable pageable);
}
