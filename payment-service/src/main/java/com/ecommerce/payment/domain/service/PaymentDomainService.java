package com.ecommerce.payment.domain.service;

import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.repository.PaymentRepository;
import com.ecommerce.payment.domain.valueobject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentDomainService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    /**
     * 创建支付
     */
    public Payment createPayment(String orderId, String userId, Money amount, PaymentMethod paymentMethod) {
        Payment payment = Payment.create(
            OrderId.of(orderId),
            UserId.of(userId),
            amount,
            paymentMethod
        );
        
        return paymentRepository.save(payment);
    }
    
    /**
     * 处理支付成功
     */
    public Payment processPaymentSuccess(String paymentId, String transactionId, String paymentGateway) {
        Optional<Payment> paymentOpt = paymentRepository.findById(PaymentId.of(paymentId));
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("支付记录不存在: " + paymentId);
        }
        
        Payment payment = paymentOpt.get();
        payment.markAsPaid(transactionId, paymentGateway);
        
        return paymentRepository.save(payment);
    }
    
    /**
     * 处理支付失败
     */
    public Payment processPaymentFailure(String paymentId, String failureReason) {
        Optional<Payment> paymentOpt = paymentRepository.findById(PaymentId.of(paymentId));
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("支付记录不存在: " + paymentId);
        }
        
        Payment payment = paymentOpt.get();
        payment.markAsFailed(failureReason);
        
        return paymentRepository.save(payment);
    }
    
    /**
     * 查询支付记录
     */
    public Optional<Payment> getPayment(String paymentId) {
        return paymentRepository.findById(PaymentId.of(paymentId));
    }
    
    /**
     * 根据订单ID查询支付记录
     */
    public List<Payment> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
    
    /**
     * 根据用户ID查询支付记录
     */
    public List<Payment> getPaymentsByUserId(String userId) {
        return paymentRepository.findByUserId(userId);
    }
}