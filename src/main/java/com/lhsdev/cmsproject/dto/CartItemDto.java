package com.lhsdev.cmsproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "장바구니 항목")
public class CartItemDto {
    @Schema(description = "장바구니 항목 ID")
    private Long id;
    @Schema(description = "상품 ID")
    private Long productId;
    @Schema(description = "상품명")
    private String productName;
    @Schema(description = "상품 가격")
    private int productPrice;
    @Schema(description = "상품 유형 (NORMAL/RENTAL)")
    private String productType;
    @Schema(description = "상품 이미지 URL")
    private String imageUrl;

    @Schema(description = "수량")
    private int quantity;
    @Schema(description = "대여 시작일")
    private LocalDate rentalStartDate;
    @Schema(description = "대여 종료일")
    private LocalDate rentalEndDate;
}
