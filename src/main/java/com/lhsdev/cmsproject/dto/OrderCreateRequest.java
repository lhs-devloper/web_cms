package com.lhsdev.cmsproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "주문 생성 요청")
public class OrderCreateRequest {
    @Schema(description = "장바구니 항목 ID 목록")
    private List<Long> cartItemIds;
    @Schema(description = "결제 수단")
    private String paymentMethod;
}
