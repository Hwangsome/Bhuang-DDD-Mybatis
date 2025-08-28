package com.ecommerce.payment.domain.repository;

import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.valueobject.PaymentId;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    
    Payment save(Payment payment);
    
    Optional<Payment> findById(PaymentId paymentId);
    
    List<Payment> findByOrderId(String orderId);
    
    List<Payment> findByUserId(String userId);
    
    void delete(PaymentId paymentId);
}