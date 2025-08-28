package com.ecommerce.payment.domain.entity;

/**
 * 支付状态枚举
 * 领域概念：支付记录在其生命周期中的不同状态
 */
public enum PaymentStatus {
    
    /**
     * 待支付状态 - 支付记录已创建，等待支付
     */
    PENDING("待支付"),
    
    /**
     * 处理中状态 - 支付正在处理中
     */
    PROCESSING("处理中"),
    
    /**
     * 已支付状态 - 支付成功完成
     */
    PAID("已支付"),
    
    /**
     * 支付失败状态 - 支付处理失败
     */
    FAILED("支付失败"),
    
    /**
     * 已退款状态 - 支付已退款
     */
    REFUNDED("已退款");
    
    private final String description;
    
    PaymentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否为终态（不能再变更）
     */
    public boolean isFinalState() {
        return this == PAID || this == FAILED || this == REFUNDED;
    }
    
    /**
     * 检查是否为成功状态
     */
    public boolean isSuccessful() {
        return this == PAID;
    }
    
    /**
     * 检查是否可以退款
     */
    public boolean canBeRefunded() {
        return this == PAID;
    }
    
    /**
     * 检查是否为进行中状态
     */
    public boolean isInProgress() {
        return this == PENDING || this == PROCESSING;
    }
}