package com.lhsdev.cmsproject.domain.order;

public enum OrderStatus {
    PENDING("주문대기"),
    CONFIRMED("주문확정"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    CANCELLED("주문취소"),
    RETURNED("반품완료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
