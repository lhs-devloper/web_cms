package com.lhsdev.cmsproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "결제 정보")
public class PaymentDto {
    @Schema(description = "주문 ID")
    private Long orderId;
    @Schema(description = "결제 수단")
    private String paymentMethod;
    @Schema(description = "결제 금액")
    private int amount;
    @Schema(description = "PG사")
    private String pgProvider;
    @Schema(description = "PG 거래 ID")
    private String pgTransactionId;
    @Schema(description = "결제 상태")
    private String status;
    @Schema(description = "결제 일시")
    private LocalDateTime paidAt;
}
