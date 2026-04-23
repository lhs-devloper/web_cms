package com.lhsdev.cmsproject.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApproveResponse {
    private boolean success;
    private String pgTransactionId;
    private int amount;
    private String message;
}
