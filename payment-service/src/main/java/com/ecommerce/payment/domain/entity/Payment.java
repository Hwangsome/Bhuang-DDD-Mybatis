package com.ecommerce.payment.domain.entity;

import com.ecommerce.payment.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 支付 - 聚合根
 * 领域概念：订单的支付记录，管理支付状态和支付信息
 * 职责：支付状态管理、支付方式控制、支付安全验证
 */
public class Payment {
    
    private PaymentId id;
    private OrderId orderId;
    private UserId userId;
    private Money amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;        // 第三方支付交易ID
    private String paymentGateway;       // 支付网关
    private String failureReason;        // 失败原因
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime failedAt;
    private LocalDateTime refundedAt;
    private LocalDateTime updatedAt;
    private Long version;                // 乐观锁
    
    private Payment() {
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0L;
    }
    
    /**
     * 创建支付记录
     */
    public static Payment create(OrderId orderId, UserId userId, Money amount, PaymentMethod paymentMethod) {
        if (orderId == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (amount == null || !amount.isPositive()) {
            throw new IllegalArgumentException("支付金额必须大于0");
        }
        
        if (paymentMethod == null) {
            throw new IllegalArgumentException("支付方式不能为空");
        }
        
        Payment payment = new Payment();
        payment.id = PaymentId.generateForOrder(orderId.getValue());
        payment.orderId = orderId;
        payment.userId = userId;
        payment.amount = amount;
        payment.paymentMethod = paymentMethod;
        
        return payment;
    }
    
    /**
     * 支付成功
     */
    public void markAsPaid(String transactionId, String paymentGateway) {
        if (status != PaymentStatus.PENDING && status != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("只有待支付或处理中的支付记录才能标记为成功");
        }
        
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("交易ID不能为空");
        }
        
        this.status = PaymentStatus.PAID;
        this.transactionId = transactionId.trim();
        this.paymentGateway = paymentGateway;
        this.paidAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.failureReason = null; // 清除失败原因
    }
    
    /**
     * 支付失败
     */
    public void markAsFailed(String failureReason) {
        if (status != PaymentStatus.PENDING && status != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("只有待支付或处理中的支付记录才能标记为失败");
        }
        
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
        this.failedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 开始处理支付
     */
    public void startProcessing() {
        if (status != PaymentStatus.PENDING) {
            throw new IllegalStateException("只有待支付状态才能开始处理");
        }
        
        this.status = PaymentStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 退款
     */
    public void refund(String reason) {
        if (status != PaymentStatus.PAID) {
            throw new IllegalStateException("只有已支付的记录才能退款");
        }
        
        this.status = PaymentStatus.REFUNDED;
        this.failureReason = reason;
        this.refundedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查是否可以支付
     */
    public boolean canBePaid() {
        return status == PaymentStatus.PENDING || status == PaymentStatus.PROCESSING;
    }
    
    /**
     * 检查是否已支付
     */
    public boolean isPaid() {
        return status == PaymentStatus.PAID;
    }
    
    /**
     * 检查是否失败
     */
    public boolean isFailed() {
        return status == PaymentStatus.FAILED;
    }
    
    /**
     * 检查是否已退款
     */
    public boolean isRefunded() {
        return status == PaymentStatus.REFUNDED;
    }
    
    // Getters
    public PaymentId getId() { return id; }
    public OrderId getOrderId() { return orderId; }
    public UserId getUserId() { return userId; }
    public Money getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public String getPaymentGateway() { return paymentGateway; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public LocalDateTime getFailedAt() { return failedAt; }
    public LocalDateTime getRefundedAt() { return refundedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", amount=" + amount +
                ", status=" + status +
                '}';
    }
}