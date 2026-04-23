package com.lhsdev.cmsproject.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReadyResponse {
    private String redirectUrl;
    private String pgTransactionId;
}
