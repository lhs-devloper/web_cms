package com.lhsdev.cmsproject.domain.product;

import com.lhsdev.cmsproject.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    private Integer stockQuantity;

    private Integer rentalAvailableCount;

    private boolean active = true;

    private int pointReward = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Builder
    public Product(String name, int price, String description, ProductCategory category,
            Integer stockQuantity, Integer rentalAvailableCount, List<String> imageUrls, boolean active, int pointReward) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.rentalAvailableCount = rentalAvailableCount;
        this.imageUrls = imageUrls;
        this.active = active;
        this.pointReward = pointReward;
    }

    public void decreaseStock(int quantity) {
        if (this.stockQuantity == null || this.stockQuantity < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stockQuantity -= quantity;
    }

    public void increaseStock(int quantity) {
        if (this.stockQuantity == null) {
            this.stockQuantity = 0;
        }
        this.stockQuantity += quantity;
    }

    public void decreaseRentalCount(int quantity) {
        if (this.rentalAvailableCount == null || this.rentalAvailableCount < quantity) {
            throw new IllegalStateException("대여 가능 수량이 부족합니다.");
        }
        this.rentalAvailableCount -= quantity;
    }

    public void increaseRentalCount(int quantity) {
        if (this.rentalAvailableCount == null) {
            this.rentalAvailableCount = 0;
        }
        this.rentalAvailableCount += quantity;
    }

    public void update(String name, int price, String description, ProductCategory category, Integer stockQuantity,
            Integer rentalAvailableCount, List<String> imageUrls, boolean active, int pointReward) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.rentalAvailableCount = rentalAvailableCount;
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.clear();
        if (imageUrls != null) {
            this.imageUrls.addAll(imageUrls);
        }
        this.active = active;
        this.pointReward = pointReward;
    }
}
