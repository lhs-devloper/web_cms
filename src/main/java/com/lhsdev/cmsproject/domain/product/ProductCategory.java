package com.lhsdev.cmsproject.domain.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code; // NORMAL, RENTAL, DIGITAL, etc.

    @Column(nullable = false, length = 100)
    private String name; // 일반상품, 대여상품, etc.

    @Column(length = 255)
    private String description;

    @Builder.Default
    private boolean hasStock = false; // 재고 관리 여부

    @Builder.Default
    private boolean hasRentalPeriod = false; // 대여 기간 관리 여부

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private int sortOrder = 0;
}
