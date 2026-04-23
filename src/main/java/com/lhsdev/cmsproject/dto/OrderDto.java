package com.lhsdev.cmsproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "주문 정보")
public class OrderDto {
    @Schema(description = "주문 ID")
    private Long id;
    @Schema(description = "주문번호")
    private String orderNumber;
    @Schema(description = "주문 상태")
    private String status;
    @Schema(description = "총 금액")
    private int totalPrice;
    @Schema(description = "주문자명")
    private String userName;
    @Schema(description = "주문자 이메일")
    private String userEmail;
    @Schema(description = "주문일시")
    private LocalDateTime createdAt;
    @Schema(description = "주문 항목 목록")
    private List<OrderItemDto> orderItems;
    @Schema(description = "결제 수단")
    private String paymentMethod;
    @Schema(description = "결제 상태")
    private String paymentStatus;
    @Schema(description = "운송장 번호")
    private String trackingNumber;
    @Schema(description = "택배사")
    private String trackingCarrier;
    @Schema(description = "배송 메모")
    private String trackingMemo;
    @Schema(description = "발송일시")
    private LocalDateTime shippedAt;
}
