package com.ecommerce.notification.domain.entity;

import com.ecommerce.notification.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 通知 - 聚合根
 * 领域概念：系统发送给用户的通知消息
 * 职责：通知状态管理、通知内容控制、发送渠道管理
 */
public class Notification {
    
    private NotificationId id;
    private UserId userId;
    private NotificationType type;
    private NotificationChannel channel;
    private String title;
    private String content;
    private String templateId;
    private String templateParams;
    private NotificationStatus status;
    private String recipient;            // 接收方（邮箱、手机号等）
    private String failureReason;        // 失败原因
    private int retryCount;              // 重试次数
    private LocalDateTime scheduledAt;   // 计划发送时间
    private LocalDateTime sentAt;        // 实际发送时间
    private LocalDateTime readAt;        // 阅读时间
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
    
    private Notification() {
        this.status = NotificationStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0L;
    }
    
    /**
     * 创建通知
     */
    public static Notification create(UserId userId, NotificationType type, NotificationChannel channel,
                                    String title, String content, String recipient) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("通知类型不能为空");
        }
        
        if (channel == null) {
            throw new IllegalArgumentException("通知渠道不能为空");
        }
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("通知标题不能为空");
        }
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("通知内容不能为空");
        }
        
        if (recipient == null || recipient.trim().isEmpty()) {
            throw new IllegalArgumentException("接收方不能为空");
        }
        
        Notification notification = new Notification();
        notification.id = NotificationId.generate();
        notification.userId = userId;
        notification.type = type;
        notification.channel = channel;
        notification.title = title.trim();
        notification.content = content.trim();
        notification.recipient = recipient.trim();
        notification.scheduledAt = LocalDateTime.now();
        
        return notification;
    }
    
    /**
     * 标记为已发送
     */
    public void markAsSent() {
        if (status != NotificationStatus.PENDING && status != NotificationStatus.SENDING) {
            throw new IllegalStateException("只有待发送或发送中的通知才能标记为已发送");
        }
        
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.failureReason = null;
    }
    
    /**
     * 标记为发送失败
     */
    public void markAsFailed(String failureReason) {
        if (status == NotificationStatus.SENT || status == NotificationStatus.READ) {
            throw new IllegalStateException("已发送或已读的通知不能标记为失败");
        }
        
        this.status = NotificationStatus.FAILED;
        this.failureReason = failureReason;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 开始发送
     */
    public void startSending() {
        if (status != NotificationStatus.PENDING) {
            throw new IllegalStateException("只有待发送的通知才能开始发送");
        }
        
        this.status = NotificationStatus.SENDING;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 标记为已读
     */
    public void markAsRead() {
        if (status != NotificationStatus.SENT) {
            throw new IllegalStateException("只有已发送的通知才能标记为已读");
        }
        
        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查是否可以重试
     */
    public boolean canRetry(int maxRetries) {
        return status == NotificationStatus.FAILED && retryCount < maxRetries;
    }
    
    /**
     * 检查是否已发送
     */
    public boolean isSent() {
        return status == NotificationStatus.SENT || status == NotificationStatus.READ;
    }
    
    /**
     * 检查是否已读
     */
    public boolean isRead() {
        return status == NotificationStatus.READ;
    }
    
    /**
     * 检查是否失败
     */
    public boolean isFailed() {
        return status == NotificationStatus.FAILED;
    }
    
    // Getters
    public NotificationId getId() { return id; }
    public UserId getUserId() { return userId; }
    public NotificationType getType() { return type; }
    public NotificationChannel getChannel() { return channel; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTemplateId() { return templateId; }
    public String getTemplateParams() { return templateParams; }
    public NotificationStatus getStatus() { return status; }
    public String getRecipient() { return recipient; }
    public String getFailureReason() { return failureReason; }
    public int getRetryCount() { return retryCount; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public LocalDateTime getSentAt() { return sentAt; }
    public LocalDateTime getReadAt() { return readAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", channel=" + channel +
                ", status=" + status +
                '}';
    }
}