package com.lhsdev.cmsproject.domain.product;

import com.lhsdev.cmsproject.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer price;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ProductStockType stockType;

    private Integer stockQuantity;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    /*
     * @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch =
     * FetchType.LAZY, orphanRemoval = true)
     * private ProgramSchedule programSchedule;
     */

    @Builder
    public Product(ProductCategory category, String name, String description, Integer price, String imageUrl,
            ProductStockType stockType, Integer stockQuantity, Boolean isActive, ProductType productType) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stockType = stockType;
        this.stockQuantity = stockQuantity;
        this.isActive = isActive;
        this.productType = productType != null ? productType : ProductType.NORMAL;
    }

    public void update(ProductCategory category, String name, String description, Integer price, String imageUrl,
            ProductStockType stockType, Integer stockQuantity, Boolean isActive, ProductType productType) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            this.imageUrl = imageUrl;
        }
        this.stockType = stockType;
        this.stockQuantity = stockQuantity;
        this.isActive = isActive;
        this.productType = productType;
    }

    /*
     * public void setProgramSchedule(ProgramSchedule schedule) {
     * this.programSchedule = schedule;
     * if (schedule != null) {
     * schedule.setProduct(this);
     * }
     * }
     */
}
