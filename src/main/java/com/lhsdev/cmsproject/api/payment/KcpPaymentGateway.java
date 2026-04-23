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
public class KcpPaymentGateway implements PaymentGateway {

    private final PaymentConfigRepository paymentConfigRepository;

    private PaymentConfig getConfig() {
        return paymentConfigRepository.findByProvider("KCP")
                .orElseThrow(() -> new IllegalStateException("KCP 결제 설정이 없습니다."));
    }

    private boolean hasKeys(PaymentConfig config) {
        return config.getClientKey() != null && !config.getClientKey().isEmpty()
                && config.getSecretKey() != null && !config.getSecretKey().isEmpty();
    }

    private String getAuthHeader(PaymentConfig config) {
        return "Basic " + Base64.getEncoder().encodeToString((config.getSecretKey() + ":").getBytes());
    }

    @Override
    public PaymentReadyResponse ready(Order order, PaymentMethod method) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentReadyResponse(
                    "/api/payments/kcp/return?orderNumber=" + order.getOrderNumber(),
                    "KCP_" + order.getOrderNumber()
            );
        }

        RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();

        String payType = (method == PaymentMethod.BANK_TRANSFER) ? "BANK" : "CARD";

        Map<String, Object> body = Map.of(
                "site_cd", config.getClientKey(),
                "order_no", order.getOrderNumber(),
                "good_name", "주문 " + order.getOrderNumber(),
                "good_mny", String.valueOf(order.getTotalPrice()),
                "pay_method", payType,
                "return_url", "/api/payments/kcp/return?orderNumber=" + order.getOrderNumber()
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/v1/pay/ready")
                    .header("Authorization", getAuthHeader(config))
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String redirectUrl = (String) response.get("redirect_url");
            String tno = (String) response.get("tno");

            return new PaymentReadyResponse(
                    redirectUrl != null ? redirectUrl : "/api/payments/kcp/return?orderNumber=" + order.getOrderNumber(),
                    tno != null ? tno : "KCP_" + order.getOrderNumber()
            );
        } catch (RestClientResponseException e) {
            log.error("KCP 결제 요청 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalStateException("KCP 결제 요청에 실패했습니다: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public PaymentApproveResponse approve(String pgToken, Order order, Payment payment) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentApproveResponse(
                    true, "KCP_" + order.getOrderNumber(),
                    order.getTotalPrice(), "결제가 완료되었습니다."
            );
        }

        RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();

        String tno = payment.getPgTransactionId();

        Map<String, Object> body = Map.of(
                "site_cd", config.getClientKey(),
                "tno", tno != null ? tno : "",
                "order_no", order.getOrderNumber(),
                "pay_amount", String.valueOf(order.getTotalPrice())
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/v1/pay/approve")
                    .header("Authorization", getAuthHeader(config))
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String resultCode = (String) response.get("res_cd");
            String resTno = (String) response.get("tno");

            if ("0000".equals(resultCode)) {
                return new PaymentApproveResponse(
                        true,
                        resTno != null ? resTno : tno,
                        order.getTotalPrice(),
                        "결제가 완료되었습니다."
                );
            } else {
                String resMsg = (String) response.get("res_msg");
                return new PaymentApproveResponse(false, null, 0,
                        "KCP 결제 승인 실패: " + (resMsg != null ? resMsg : resultCode));
            }
        } catch (RestClientResponseException e) {
            log.error("KCP 결제 승인 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new PaymentApproveResponse(false, null, 0,
                    "KCP 결제 승인에 실패했습니다: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public PaymentCancelResponse cancel(Payment payment) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentCancelResponse(true, payment.getAmount(), "결제가 취소되었습니다.");
        }

        RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();

        Map<String, Object> body = Map.of(
                "site_cd", config.getClientKey(),
                "tno", payment.getPgTransactionId(),
                "mod_type", "STSC",
                "mod_mny", String.valueOf(payment.getAmount()),
                "rem_mny", "0"
        );

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/v1/pay/cancel")
                    .header("Authorization", getAuthHeader(config))
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String resultCode = (String) response.get("res_cd");

            if ("0000".equals(resultCode)) {
                return new PaymentCancelResponse(true, payment.getAmount(), "결제가 취소되었습니다.");
            } else {
                String resMsg = (String) response.get("res_msg");
                return new PaymentCancelResponse(false, 0,
                        "KCP 결제 취소 실패: " + (resMsg != null ? resMsg : resultCode));
            }
        } catch (RestClientResponseException e) {
            log.error("KCP 결제 취소 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new PaymentCancelResponse(false, 0,
                    "KCP 결제 취소에 실패했습니다: " + e.getResponseBodyAsString());
        }
    }
}
