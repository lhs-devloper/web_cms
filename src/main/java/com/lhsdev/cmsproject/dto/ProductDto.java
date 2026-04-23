package com.lhsdev.cmsproject.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@Schema(description = "상품 정보")
public class ProductDto {
    @Schema(description = "상품 ID")
    private Long id;
    @Schema(description = "상품명")
    private String name;
    @Schema(description = "가격")
    private int price;
    @Schema(description = "상품 설명")
    private String description;
    @Schema(description = "상품 유형 코드")
    private String type;
    @Schema(description = "카테고리 ID")
    private Long categoryId;
    @Schema(description = "카테고리명")
    private String categoryName;
    @Schema(description = "재고 관리 여부")
    private boolean hasStock;
    @Schema(description = "대여 기간 관리 여부")
    private boolean hasRentalPeriod;
    @Schema(description = "재고 수량")
    private Integer stockQuantity;
    @Schema(description = "대여 가능 수량")
    private Integer rentalAvailableCount;
    @Schema(description = "활성 여부")
    private boolean active;
    @Schema(description = "상품 이미지 URL 목록")
    private List<String> imageUrls = new ArrayList<>();
    @Schema(description = "구매 시 적립 포인트")
    private int pointReward;
}
