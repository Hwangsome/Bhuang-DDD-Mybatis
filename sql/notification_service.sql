-- Notification Service Database Schema

CREATE DATABASE IF NOT EXISTS ecommerce_notification DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_notification;

-- Notification table
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notification_id VARCHAR(64) NOT NULL UNIQUE COMMENT '通知ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    type VARCHAR(32) NOT NULL COMMENT '通知类型',
    channel VARCHAR(32) NOT NULL COMMENT '通知渠道',
    title VARCHAR(255) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    status VARCHAR(32) NOT NULL COMMENT '通知状态',
    sent_at TIMESTAMP NULL COMMENT '发送时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_channel (channel),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- Sample data
INSERT INTO notification (notification_id, user_id, type, channel, title, content, status) VALUES
('NOTIF_001', 'USER_001', 'ORDER_CREATED', 'EMAIL', '订单创建成功', '您的订单ORD_001已创建成功，请及时支付。', 'SENT'),
('NOTIF_002', 'USER_001', 'PAYMENT_SUCCESS', 'SMS', '支付成功', '您的订单ORD_001支付成功，金额299.99元。', 'PENDING'),
('NOTIF_003', 'USER_002', 'ORDER_CREATED', 'PUSH', '订单创建', '您有新的订单ORD_002待支付。', 'SENT');