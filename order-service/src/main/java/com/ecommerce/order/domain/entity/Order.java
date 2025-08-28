package com.ecommerce.order.domain.entity;

import com.ecommerce.order.domain.valueobject.*;

import java.time.LocalDateTime;

/**
 * 订单 - 聚合根
 */
public class Order {
    
    public enum OrderStatus {
        PENDING, CONFIRMED, PAID, SHIPPED, DELIVERED, CANCELLED
    }
    
    private OrderId orderId;
    private UserId userId;
    private Money totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Order() {
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public static Order create(UserId userId, Money totalAmount) {
        Order order = new Order();
        order.orderId = OrderId.generate();
        order.userId = userId;
        order.totalAmount = totalAmount;
        return order;
    }
    
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待确认的订单才能确认");
        }
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void pay() {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("只有已确认的订单才能支付");
        }
        this.status = OrderStatus.PAID;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("只有已支付的订单才能发货");
        }
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("只有已发货的订单才能确认收货");
        }
        this.status = OrderStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("已送达的订单不能取消");
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public OrderId getOrderId() { return orderId; }
    public UserId getUserId() { return userId; }
    public Money getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}