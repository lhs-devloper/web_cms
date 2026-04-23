package com.lhsdev.cmsproject.service;

import com.lhsdev.cmsproject.api.payment.KakaoPaymentGateway;
import com.lhsdev.cmsproject.api.payment.KcpPaymentGateway;
import com.lhsdev.cmsproject.api.payment.PaymentGateway;
import com.lhsdev.cmsproject.api.payment.TossPaymentGateway;
import com.lhsdev.cmsproject.domain.order.Order;
import com.lhsdev.cmsproject.domain.order.OrderStatus;
import com.lhsdev.cmsproject.domain.payment.Payment;
import com.lhsdev.cmsproject.domain.payment.PaymentMethod;
import com.lhsdev.cmsproject.domain.payment.PaymentStatus;
import com.lhsdev.cmsproject.domain.product.Product;
import com.lhsdev.cmsproject.dto.PaymentApproveRequest;
import com.lhsdev.cmsproject.dto.PaymentDto;
import com.lhsdev.cmsproject.dto.payment.PaymentApproveResponse;
import com.lhsdev.cmsproject.dto.payment.PaymentCancelResponse;
import com.lhsdev.cmsproject.dto.payment.PaymentReadyResponse;
import com.lhsdev.cmsproject.repository.OrderRepository;
import com.lhsdev.cmsproject.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private KcpPaymentGateway kcpPaymentGateway;
    @Autowired
    private KakaoPaymentGateway kakaoPaymentGateway;
    @Autowired
    private TossPaymentGateway tossPaymentGateway;

    @Transactional
    public PaymentReadyResponse requestPayment(Long orderId, PaymentMethod method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("결제 가능한 상태가 아닙니다.");
        }

        String pgProvider = getPgProvider(method);
        PaymentGateway gateway = getGateway(method);

        // 기존 Payment 확인 (재결제 지원)
        Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);

        if (payment != null) {
            if (payment.getStatus() == PaymentStatus.PAID) {
                throw new IllegalStateException("이미 결제된 주문입니다.");
            }
            // READY 또는 FAILED 상태 → 기존 Payment 재사용
            payment.setPaymentMethod(method);
            payment.setStatus(PaymentStatus.READY);
            payment.setAmount(order.getTotalPrice());
            payment.setPgProvider(pgProvider);
            payment.setFailReason(null);
        } else {
            payment = Payment.builder()
                    .order(order)
                    .paymentMethod(method)
                    .status(PaymentStatus.READY)
                    .amount(order.getTotalPrice())
                    .pgProvider(pgProvider)
                    .build();
        }

        PaymentReadyResponse response = gateway.ready(order, method);
        payment.setPgTransactionId(response.getPgTransactionId());

        paymentRepository.save(payment);

        return response;
    }

    @Transactional
    public PaymentDto approvePayment(Long orderId, PaymentApproveRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        PaymentGateway gateway = getGateway(payment.getPaymentMethod());
        String token = request.getPgToken() != null ? request.getPgToken() : request.getPaymentKey();

        try {
            PaymentApproveResponse response = gateway.approve(token, order, payment);

            if (response.isSuccess()) {
                payment.markPaid(response.getPgTransactionId());
                order.updateStatus(OrderStatus.CONFIRMED);
            } else {
                payment.markFailed(response.getMessage());
                // 재고 복원
                restoreStock(order);
            }
        } catch (Exception e) {
            payment.markFailed(e.getMessage());
            restoreStock(order);
            throw new IllegalStateException("결제 승인에 실패했습니다: " + e.getMessage());
        }

        return toDto(payment);
    }

    @Transactional
    public PaymentDto cancelPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("취소할 수 없는 결제 상태입니다.");
        }

        PaymentGateway gateway = getGateway(payment.getPaymentMethod());
        PaymentCancelResponse response = gateway.cancel(payment);

        if (response.isSuccess()) {
            payment.markRefunded();
        } else {
            throw new IllegalStateException("결제 취소에 실패했습니다: " + response.getMessage());
        }

        return toDto(payment);
    }

    private void restoreStock(Order order) {
        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            if (product.getCategory().isHasStock()) {
                product.increaseStock(item.getQuantity());
            }
            if (product.getCategory().isHasRentalPeriod()) {
                product.increaseRentalCount(item.getQuantity());
            }
        });
    }

    private PaymentGateway getGateway(PaymentMethod method) {
        return switch (method) {
            case CARD, BANK_TRANSFER -> kcpPaymentGateway;
            case KAKAO_PAY -> kakaoPaymentGateway;
            case TOSS_PAY -> tossPaymentGateway;
        };
    }

    private String getPgProvider(PaymentMethod method) {
        return switch (method) {
            case CARD, BANK_TRANSFER -> "KCP";
            case KAKAO_PAY -> "KAKAOPAY";
            case TOSS_PAY -> "TOSSPAY";
        };
    }

    private PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setOrderId(payment.getOrder().getId());
        dto.setPaymentMethod(payment.getPaymentMethod().name());
        dto.setAmount(payment.getAmount());
        dto.setPgProvider(payment.getPgProvider());
        dto.setPgTransactionId(payment.getPgTransactionId());
        dto.setStatus(payment.getStatus().name());
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }
}
