package com.lhsdev.cmsproject.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class CartItemDto {
    private Long id;
    private Long productId;
    private String productName;
    private int productPrice;
    private String productType;
    private String imageUrl;

    private int quantity;
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;
}
