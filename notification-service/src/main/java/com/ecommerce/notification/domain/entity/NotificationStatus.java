package com.ecommerce.notification.domain.entity;

/**
 * 通知状态枚举
 * 领域概念：通知在其生命周期中的不同状态
 */
public enum NotificationStatus {
    
    /**
     * 待发送状态 - 通知已创建，等待发送
     */
    PENDING("待发送"),
    
    /**
     * 发送中状态 - 通知正在发送中
     */
    SENDING("发送中"),
    
    /**
     * 已发送状态 - 通知发送成功
     */
    SENT("已发送"),
    
    /**
     * 发送失败状态 - 通知发送失败
     */
    FAILED("发送失败"),
    
    /**
     * 已读状态 - 用户已阅读通知
     */
    READ("已读");
    
    private final String description;
    
    NotificationStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否为终态
     */
    public boolean isFinalState() {
        return this == SENT || this == READ || this == FAILED;
    }
    
    /**
     * 检查是否为成功状态
     */
    public boolean isSuccessful() {
        return this == SENT || this == READ;
    }
    
    /**
     * 检查是否可以重试
     */
    public boolean canRetry() {
        return this == FAILED;
    }
    
    /**
     * 检查是否为进行中状态
     */
    public boolean isInProgress() {
        return this == PENDING || this == SENDING;
    }
}