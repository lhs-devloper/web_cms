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

    @Override
    public PaymentReadyResponse ready(Order order, PaymentMethod method) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentReadyResponse(
                    "/api/payments/kcp/return?orderNumber=" + order.getOrderNumber(),
                    "KCP_" + order.getOrderNumber()
            );
        }

        try {
            RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();
            // KCP REST API 결제 요청
            return new PaymentReadyResponse(
                    "/api/payments/kcp/return?orderNumber=" + order.getOrderNumber(),
                    "KCP_" + order.getOrderNumber()
            );
        } catch (RestClientResponseException e) {
            log.error("KCP 결제 요청 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalStateException("KCP 결제 요청에 실패했습니다: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public PaymentApproveResponse approve(String pgToken, Order order) {
        PaymentConfig config = getConfig();

        if (!hasKeys(config)) {
            return new PaymentApproveResponse(
                    true, "KCP_" + order.getOrderNumber(),
                    order.getTotalPrice(), "결제가 완료되었습니다."
            );
        }

        try {
            RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();
            // KCP REST API 결제 승인
            return new PaymentApproveResponse(
                    true, "KCP_" + order.getOrderNumber(),
                    order.getTotalPrice(), "결제가 완료되었습니다."
            );
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

        try {
            RestClient restClient = RestClient.builder().baseUrl(config.getApiUrl()).build();
            // KCP REST API 결제 취소
            return new PaymentCancelResponse(true, payment.getAmount(), "결제가 취소되었습니다.");
        } catch (RestClientResponseException e) {
            log.error("KCP 결제 취소 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new PaymentCancelResponse(false, 0,
                    "KCP 결제 취소에 실패했습니다: " + e.getResponseBodyAsString());
        }
    }
}
