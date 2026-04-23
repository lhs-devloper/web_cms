package com.lhsdev.cmsproject.api.payment;

import com.lhsdev.cmsproject.domain.order.Order;
import com.lhsdev.cmsproject.domain.payment.Payment;
import com.lhsdev.cmsproject.domain.payment.PaymentMethod;
import com.lhsdev.cmsproject.dto.payment.PaymentApproveResponse;
import com.lhsdev.cmsproject.dto.payment.PaymentCancelResponse;
import com.lhsdev.cmsproject.dto.payment.PaymentReadyResponse;

public interface PaymentGateway {
    PaymentReadyResponse ready(Order order, PaymentMethod method);
    PaymentApproveResponse approve(String pgToken, Order order);
    PaymentCancelResponse cancel(Payment payment);
}
