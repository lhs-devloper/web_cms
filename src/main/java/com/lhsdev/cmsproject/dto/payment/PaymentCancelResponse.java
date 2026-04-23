package com.lhsdev.cmsproject.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelResponse {
    private boolean success;
    private int refundAmount;
    private String message;
}
