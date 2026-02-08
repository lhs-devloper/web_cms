package com.lhsdev.cmsproject.domain.log;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_logs", indexes = {
        @Index(name = "idx_visitor_log_date", columnList = "visitDate")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false)
    private String requestUrl;

    @Column(nullable = false)
    private LocalDate visitDate;

    @Column(nullable = false)
    private LocalDateTime visitTime;
}
