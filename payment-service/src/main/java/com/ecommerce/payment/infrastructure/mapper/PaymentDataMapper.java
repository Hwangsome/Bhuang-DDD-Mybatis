package com.ecommerce.payment.infrastructure.mapper;

import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.infrastructure.entity.PaymentPO;

public interface PaymentDataMapper {
    
    PaymentPO paymentToPaymentPO(Payment payment);
    
    Payment paymentPOToPayment(PaymentPO paymentPO);
}