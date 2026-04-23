package com.lhsdev.cmsproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "결제 승인 요청")
public class PaymentApproveRequest {
    @Schema(description = "PG 토큰")
    private String pgToken;
    @Schema(description = "결제 키")
    private String paymentKey;
    @Schema(description = "주문 ID")
    private String orderId;
    @Schema(description = "결제 금액")
    private int amount;
}
