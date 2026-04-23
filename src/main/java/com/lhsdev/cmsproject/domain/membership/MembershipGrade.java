package com.lhsdev.cmsproject.domain.membership;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "membership_grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Builder.Default
    private int minPoints = 0;

    @Builder.Default
    private double pointRate = 1.0;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(length = 20)
    @Builder.Default
    private String color = "#94a3b8";

    @Builder.Default
    private int sortOrder = 0;

    @Builder.Default
    private boolean isActive = true;
}
