package com.lhsdev.cmsproject.domain.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "program_schedules")
@Getter
@Setter
@NoArgsConstructor
public class ProgramSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 운영 기간 ---
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate operationStartDate;
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate operationEndDate;

    // --- 예약 가능 기간 ---
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationStartDate;
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationEndDate;

    // --- 예약 스타일 ---
    // true: 상시 오픈 (날짜 상관없음), false: 정해진 날짜만
    private Boolean isAlwaysOpen;

    // --- 예약 설정 ---
    // 예약 가능 시간대 (예: "09:00-18:00") - 단순화를 위해 문자열로 처리하거나 시작/종료 시간 필드 사용
    @org.springframework.format.annotation.DateTimeFormat(pattern = "HH:mm")
    private LocalTime availableStartTime;
    @org.springframework.format.annotation.DateTimeFormat(pattern = "HH:mm")
    private LocalTime availableEndTime;

    private Integer maxParticipants; // 최대 예약 인원

    // --- 정책 ---
    @Enumerated(EnumType.STRING)
    private PricePolicy pricePolicy;

    @Enumerated(EnumType.STRING)
    private ApprovalType approvalType;

    // --- 제약 사항 ---
    private Integer minDurationMinutes; // 최소 이용 시간 (분)
    private Integer maxDurationMinutes; // 최대 이용 시간 (분)
    private Integer timeUnitMinutes; // 예약 단위 (예: 30분, 60분)

    private Integer bufferTimeMinutes; // 준비/정비 시간

    // --- 연관 관계 ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 편의 메서드
    public void setProduct(Product product) {
        this.product = product;
    }
}
