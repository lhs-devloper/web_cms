package com.lhsdev.cmsproject.domain.order;

import com.lhsdev.cmsproject.domain.BaseTimeEntity;
import com.lhsdev.cmsproject.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    private String trackingNumber;
    private String trackingCarrier;
    @Column(columnDefinition = "TEXT")
    private String trackingMemo;
    private LocalDateTime shippedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    public Order(User user, OrderStatus status, int totalPrice, String orderNumber) {
        this.user = user;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderNumber = orderNumber;
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void updateTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public void updateTracking(String trackingNumber, String trackingCarrier, String trackingMemo) {
        this.trackingNumber = trackingNumber;
        this.trackingCarrier = trackingCarrier;
        this.trackingMemo = trackingMemo;
        if (trackingNumber != null && !trackingNumber.isEmpty() && this.shippedAt == null) {
            this.shippedAt = LocalDateTime.now();
        }
    }
}
