package com.lhsdev.cmsproject.dto;

import com.lhsdev.cmsproject.domain.product.ProductType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private int price;
    private String description;
    private ProductType type;
    private Integer stockQuantity;
    private Integer rentalAvailableCount;
    private boolean active;
    private List<String> imageUrls = new ArrayList<>();
}
