package com.lhsdev.cmsproject.domain.product;

public enum ProductType {
    NORMAL("일반상품"),
    RENTAL("대여상품");

    private final String description;

    ProductType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
