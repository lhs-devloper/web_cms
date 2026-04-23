package com.lhsdev.cmsproject.domain.payment;

import com.lhsdev.cmsproject.domain.BaseTimeEntity;
import com.lhsdev.cmsproject.domain.order.Order;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private int amount;

    private String pgProvider;

    private String pgTransactionId;

    private LocalDateTime paidAt;

    private LocalDateTime cancelledAt;

    private String failReason;

    @Builder
    public Payment(Order order, PaymentMethod paymentMethod, PaymentStatus status,
                   int amount, String pgProvider) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.amount = amount;
        this.pgProvider = pgProvider;
    }

    public void markPaid(String pgTransactionId) {
        this.status = PaymentStatus.PAID;
        this.pgTransactionId = pgTransactionId;
        this.paidAt = LocalDateTime.now();
    }

    public void markFailed(String failReason) {
        this.status = PaymentStatus.FAILED;
        this.failReason = failReason;
    }

    public void markCancelled() {
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void setPgTransactionId(String pgTransactionId) {
        this.pgTransactionId = pgTransactionId;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPgProvider(String pgProvider) {
        this.pgProvider = pgProvider;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
