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

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoPaymentGateway implements PaymentGateway {

    private final PaymentConfigRepository paymentConfigRepository;

    private PaymentConfig getConfig() {
        return paymentConfigRepository.findByProvider("KAKAOPAY")
                .orElseThrow(() -> new IllegalStateException("카카오페이 결제 설정이 없습니다."));
    }

    private boolean hasKeys(PaymentConfig config) {
        return config.getClientKey() != null && !config.getClientKey().isEmpty();
    }

    private String getCid(PaymentConfig config) {
        return (config.getCid() != null && !config.getCid().isEmpty()) ? config.getCid() : "TC0ONETIME";
    }

    @Override
    public PaymentReadyResponse ready(Order order, PaymentMethod method) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentReadyResponse(
                    "/api/payments/kakao/success?orderNumber=" + order.getOrderNumber(),
                    "KAKAO_" + order.getOrderNumber()
            );
        }

        String cid = getCid(config);
        RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();

        Map<String, Object> body = Map.of(
                "cid", cid,
                "partner_order_id", order.getOrderNumber(),
                "partner_user_id", order.getUser().getId().toString(),
                "item_name", "주문 " + order.getOrderNumber(),
                "quantity", 1,
                "total_amount", order.getTotalPrice(),
                "tax_free_amount", 0,
                "approval_url", "/api/payments/kakao/success?orderNumber=" + order.getOrderNumber(),
                "cancel_url", "/api/payments/kakao/cancel?orderNumber=" + order.getOrderNumber(),
                "fail_url", "/api/payments/kakao/fail?orderNumber=" + order.getOrderNumber()
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/online/v1/payment/ready")
                    .header("Authorization", "SECRET_KEY " + config.getClientKey())
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            return new PaymentReadyResponse(
                    (String) response.get("next_redirect_pc_url"),
                    (String) response.get("tid")
            );
        } catch (RestClientResponseException e) {
            log.error("카카오페이 결제 요청 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalStateException("카카오페이 결제 요청에 실패했습니다: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public PaymentApproveResponse approve(String pgToken, Order order, Payment payment) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentApproveResponse(
                    true, "KAKAO_" + order.getOrderNumber(),
                    order.getTotalPrice(), "결제가 완료되었습니다."
            );
        }

        String cid = getCid(config);
        String tid = payment.getPgTransactionId(); // ready()에서 저장된 tid
        RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();

        Map<String, Object> body = Map.of(
                "cid", cid,
                "tid", tid,
                "partner_order_id", order.getOrderNumber(),
                "partner_user_id", order.getUser().getId().toString(),
                "pg_token", pgToken
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/online/v1/payment/approve")
                    .header("Authorization", "SECRET_KEY " + config.getClientKey())
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> amountMap = (Map<String, Object>) response.get("amount");
            return new PaymentApproveResponse(
                    true,
                    (String) response.get("tid"),
                    ((Number) amountMap.get("total")).intValue(),
                    "결제가 완료되었습니다."
            );
        } catch (RestClientResponseException e) {
            log.error("카카오페이 결제 승인 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new PaymentApproveResponse(false, null, 0,
                    "카카오페이 결제 승인에 실패했습니다: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public PaymentCancelResponse cancel(Payment payment) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentCancelResponse(true, payment.getAmount(), "결제가 취소되었습니다.");
        }

        String cid = getCid(config);
        RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();

        Map<String, Object> body = Map.of(
                "cid", cid,
                "tid", payment.getPgTransactionId(),
                "cancel_amount", payment.getAmount(),
                "cancel_tax_free_amount", 0
        );

        try {
            restClient.post()
                    .uri("/online/v1/payment/cancel")
                    .header("Authorization", "SECRET_KEY " + config.getClientKey())
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            return new PaymentCancelResponse(true, payment.getAmount(), "결제가 취소되었습니다.");
        } catch (RestClientResponseException e) {
            log.error("카카오페이 결제 취소 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new PaymentCancelResponse(false, 0,
                    "카카오페이 결제 취소에 실패했습니다: " + e.getResponseBodyAsString());
        }
    }
}
