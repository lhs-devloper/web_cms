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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    private Integer stockQuantity;

    private Integer rentalAvailableCount;

    private boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Builder
    public Product(String name, int price, String description, ProductType type,
            Integer stockQuantity, Integer rentalAvailableCount, List<String> imageUrls, boolean active) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.type = type;
        this.stockQuantity = stockQuantity;
        this.rentalAvailableCount = rentalAvailableCount;
        this.imageUrls = imageUrls;
        this.active = active;
    }

    public void update(String name, int price, String description, ProductType type, Integer stockQuantity,
            Integer rentalAvailableCount, List<String> imageUrls, boolean active) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.type = type;
        this.stockQuantity = stockQuantity;
        this.rentalAvailableCount = rentalAvailableCount;
        this.imageUrls = imageUrls;
        this.active = active;
    }
}
