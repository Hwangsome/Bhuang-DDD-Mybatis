package com.ecommerce.order.domain.entity;

/**
 * 订单状态枚举
 * 领域概念：订单在其生命周期中的不同状态
 */
public enum OrderStatus {
    
    /**
     * 待确认状态 - 订单刚创建，等待确认
     */
    PENDING("待确认"),
    
    /**
     * 已确认状态 - 订单已确认，等待支付
     */
    CONFIRMED("已确认"),
    
    /**
     * 已支付状态 - 订单已支付，等待发货
     */
    PAID("已支付"),
    
    /**
     * 已发货状态 - 订单已发货，等待收货
     */
    SHIPPED("已发货"),
    
    /**
     * 已完成状态 - 订单已收货完成
     */
    COMPLETED("已完成"),
    
    /**
     * 已取消状态 - 订单已取消
     */
    CANCELLED("已取消");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否为终态（不能再变更）
     */
    public boolean isFinalState() {
        return this == COMPLETED || this == CANCELLED;
    }
    
    /**
     * 检查是否可以支付
     */
    public boolean canBePaid() {
        return this == CONFIRMED;
    }
    
    /**
     * 检查是否可以发货
     */
    public boolean canBeShipped() {
        return this == PAID;
    }
    
    /**
     * 检查是否可以取消
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED;
    }
    
    /**
     * 检查是否可以确认收货
     */
    public boolean canBeCompleted() {
        return this == SHIPPED;
    }
    
    /**
     * 检查是否为进行中的订单
     */
    public boolean isInProgress() {
        return !isFinalState();
    }
}