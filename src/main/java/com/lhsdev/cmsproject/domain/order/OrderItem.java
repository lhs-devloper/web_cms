package com.lhsdev.cmsproject.domain.order;

import com.lhsdev.cmsproject.domain.BaseTimeEntity;
import com.lhsdev.cmsproject.domain.product.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int snapshotPrice;

    @Column(nullable = false)
    private String snapshotProductName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int subtotal;

    @Column(nullable = false)
    private String productType;

    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;

    @Builder
    public OrderItem(Product product, int snapshotPrice, String snapshotProductName,
                     int quantity, int subtotal, String productType,
                     LocalDate rentalStartDate, LocalDate rentalEndDate) {
        this.product = product;
        this.snapshotPrice = snapshotPrice;
        this.snapshotProductName = snapshotProductName;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.productType = productType;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
    }

    void setOrder(Order order) {
        this.order = order;
    }
}
