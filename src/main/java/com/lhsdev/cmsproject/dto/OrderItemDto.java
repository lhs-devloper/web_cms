package com.lhsdev.cmsproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Schema(description = "주문 항목")
public class OrderItemDto {
    @Schema(description = "주문 항목 ID")
    private Long id;
    @Schema(description = "상품 ID")
    private Long productId;
    @Schema(description = "상품명")
    private String productName;
    @Schema(description = "단가")
    private int price;
    @Schema(description = "수량")
    private int quantity;
    @Schema(description = "소계")
    private int subtotal;
    @Schema(description = "상품 유형")
    private String productType;
    @Schema(description = "상품 이미지 URL")
    private String imageUrl;
    @Schema(description = "상품 이미지 URL 목록")
    private List<String> imageUrls;
    @Schema(description = "대여 시작일")
    private LocalDate rentalStartDate;
    @Schema(description = "대여 종료일")
    private LocalDate rentalEndDate;
}
