package com.lhsdev.cmsproject.domain.payment;

public enum PaymentMethod {
    CARD("신용/체크카드"),
    KAKAO_PAY("카카오페이"),
    TOSS_PAY("토스페이"),
    BANK_TRANSFER("계좌이체");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
