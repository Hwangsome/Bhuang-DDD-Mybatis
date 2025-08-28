-- Payment Service Database Schema

CREATE DATABASE IF NOT EXISTS ecommerce_payment DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_payment;

-- Payment table
CREATE TABLE IF NOT EXISTS payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id VARCHAR(64) NOT NULL UNIQUE COMMENT '支付ID',
    order_id VARCHAR(64) NOT NULL COMMENT '订单ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY' COMMENT '货币类型',
    payment_method VARCHAR(32) NOT NULL COMMENT '支付方式',
    status VARCHAR(32) NOT NULL COMMENT '支付状态',
    transaction_id VARCHAR(128) COMMENT '第三方交易ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付表';

-- Refund table
CREATE TABLE IF NOT EXISTS refund (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    refund_id VARCHAR(64) NOT NULL UNIQUE COMMENT '退款ID',
    payment_id VARCHAR(64) NOT NULL COMMENT '支付ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    reason VARCHAR(255) COMMENT '退款原因',
    status VARCHAR(32) NOT NULL COMMENT '退款状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_payment_id (payment_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款表';

-- Sample data
INSERT INTO payment (payment_id, order_id, user_id, amount, currency, payment_method, status) VALUES
('PAY_001', 'ORD_001', 'USER_001', 299.99, 'CNY', 'ALIPAY', 'COMPLETED'),
('PAY_002', 'ORD_002', 'USER_002', 599.99, 'CNY', 'WECHAT_PAY', 'COMPLETED');