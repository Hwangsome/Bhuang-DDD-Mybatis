package com.ecommerce.payment.infrastructure.mapper;

import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.entity.PaymentStatus;
import com.ecommerce.payment.domain.valueobject.Money;
import com.ecommerce.payment.domain.valueobject.OrderId;
import com.ecommerce.payment.domain.valueobject.PaymentId;
import com.ecommerce.payment.domain.valueobject.PaymentMethod;
import com.ecommerce.payment.domain.valueobject.UserId;
import com.ecommerce.payment.infrastructure.entity.PaymentPO;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataMapperImpl implements PaymentDataMapper {

    @Override
    public PaymentPO paymentToPaymentPO(Payment payment) {
        if (payment == null) {
            return null;
        }
        
        PaymentPO paymentPO = new PaymentPO();
        paymentPO.setPaymentId(payment.getId() != null ? payment.getId().getValue() : null);
        paymentPO.setOrderId(payment.getOrderId() != null ? payment.getOrderId().getValue() : null);
        paymentPO.setUserId(payment.getUserId() != null ? payment.getUserId().getValue() : null);
        paymentPO.setAmount(payment.getAmount() != null ? payment.getAmount().getAmount() : null);
        paymentPO.setCurrency(payment.getAmount() != null ? payment.getAmount().getCurrency() : null);
        paymentPO.setPaymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : null);
        paymentPO.setStatus(payment.getStatus() != null ? payment.getStatus().name() : null);
        paymentPO.setTransactionId(payment.getTransactionId());
        
        return paymentPO;
    }

    @Override
    public Payment paymentPOToPayment(PaymentPO paymentPO) {
        if (paymentPO == null) {
            return null;
        }
        
        // Use create method and reflection to set additional fields
        Payment payment = Payment.create(
            OrderId.of(paymentPO.getOrderId()),
            UserId.of(paymentPO.getUserId()),
            Money.of(paymentPO.getAmount(), paymentPO.getCurrency()),
            PaymentMethod.valueOf(paymentPO.getPaymentMethod())
        );
        
        // Set additional fields using reflection or add setters if needed
        try {
            java.lang.reflect.Field idField = Payment.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(payment, PaymentId.of(paymentPO.getPaymentId()));
            
            java.lang.reflect.Field statusField = Payment.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(payment, PaymentStatus.valueOf(paymentPO.getStatus()));
            
            if (paymentPO.getTransactionId() != null) {
                java.lang.reflect.Field transactionIdField = Payment.class.getDeclaredField("transactionId");
                transactionIdField.setAccessible(true);
                transactionIdField.set(payment, paymentPO.getTransactionId());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to map PaymentPO to Payment", e);
        }
        
        return payment;
    }
}