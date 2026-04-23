package com.lhsdev.cmsproject.api.payment;

import com.lhsdev.cmsproject.domain.order.Order;
import com.lhsdev.cmsproject.domain.payment.Payment;
import com.lhsdev.cmsproject.domain.payment.PaymentConfig;
import com.lhsdev.cmsproject.domain.payment.PaymentMethod;
import com.lhsdev.cmsproject.dto.payment.PaymentApproveResponse;
import com.lhsdev.cmsproject.dto.payment.PaymentCancelResponse;
import com.lhsdev.cmsproject.dto.payment.PaymentReadyResponse;
import com.lhsdev.cmsproject.repository.PaymentConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentGateway implements PaymentGateway {

    private final PaymentConfigRepository paymentConfigRepository;

    private PaymentConfig getConfig() {
        return paymentConfigRepository.findByProvider("TOSSPAY")
                .orElseThrow(() -> new IllegalStateException("토스페이 결제 설정이 없습니다."));
    }

    private boolean hasKeys(PaymentConfig config) {
        return config.getSecretKey() != null && !config.getSecretKey().isEmpty();
    }

    private String getAuthHeader(PaymentConfig config) {
        return "Basic " + Base64.getEncoder().encodeToString((config.getSecretKey() + ":").getBytes());
    }

    @Override
    public PaymentReadyResponse ready(Order order, PaymentMethod method) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentReadyResponse(
                    "/api/payments/toss/success?orderNumber=" + order.getOrderNumber(),
                    "TOSS_" + order.getOrderNumber()
            );
        }

        // 토스페이먼츠는 클라이언트 SDK에서 결제창 호출
        return new PaymentReadyResponse(
                null,
                "TOSS_" + order.getOrderNumber()
        );
    }

    @Override
    public PaymentApproveResponse approve(String paymentKey, Order order, Payment payment) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentApproveResponse(
                    true, "TOSS_" + order.getOrderNumber(),
                    order.getTotalPrice(), "결제가 완료되었습니다."
            );
        }

        RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();

        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", order.getOrderNumber(),
                "amount", order.getTotalPrice()
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/v1/payments/confirm")
                    .header("Authorization", getAuthHeader(config))
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            return new PaymentApproveResponse(
                    true,
                    (String) response.get("paymentKey"),
                    ((Number) response.get("totalAmount")).intValue(),
                    "결제가 완료되었습니다."
            );
        } catch (RestClientResponseException e) {
            log.error("토스페이 결제 승인 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new PaymentApproveResponse(false, null, 0,
                    "토스페이 결제 승인에 실패했습니다: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public PaymentCancelResponse cancel(Payment payment) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentCancelResponse(true, payment.getAmount(), "결제가 취소되었습니다.");
        }

        RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();

        Map<String, String> body = Map.of("cancelReason", "주문 취소");

        try {
            restClient.post()
                    .uri("/v1/payments/{paymentKey}/cancel", payment.getPgTransactionId())
                    .header("Authorization", getAuthHeader(config))
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            return new PaymentCancelResponse(true, payment.getAmount(), "결제가 취소되었습니다.");
        } catch (RestClientResponseException e) {
            log.error("토스페이 결제 취소 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new PaymentCancelResponse(false, 0,
                    "토스페이 결제 취소에 실패했습니다: " + e.getResponseBodyAsString());
        }
    }
}
