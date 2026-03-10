package com.lhsdev.cmsproject.domain.cart;

import com.lhsdev.cmsproject.domain.BaseTimeEntity;
import com.lhsdev.cmsproject.domain.product.Product;
import com.lhsdev.cmsproject.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    // For RENTAL products: the booking dates
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;

    @Builder
    public CartItem(User user, Product product, int quantity, LocalDate rentalStartDate, LocalDate rentalEndDate) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void updateRentalDates(LocalDate start, LocalDate end) {
        this.rentalStartDate = start;
        this.rentalEndDate = end;
    }
}
