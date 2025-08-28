-- Order Service Database Schema

CREATE DATABASE IF NOT EXISTS ecommerce_order DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_order;

-- Order table
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL UNIQUE COMMENT '订单ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY' COMMENT '货币类型',
    status VARCHAR(32) NOT NULL COMMENT '订单状态',
    order_date TIMESTAMP NOT NULL COMMENT '订单日期',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- Order item table
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(64) NOT NULL COMMENT '订单ID',
    product_id VARCHAR(64) NOT NULL COMMENT '商品ID',
    sku_id VARCHAR(64) NOT NULL COMMENT 'SKU ID',
    quantity INT NOT NULL COMMENT '商品数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    total_price DECIMAL(10,2) NOT NULL COMMENT '商品总价',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    INDEX idx_sku_id (sku_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单商品表';

-- Sample data
INSERT INTO orders (order_id, user_id, total_amount, currency, status, order_date) VALUES
('ORD_001', 'USER_001', 299.99, 'CNY', 'COMPLETED', NOW()),
('ORD_002', 'USER_002', 599.99, 'CNY', 'PENDING', NOW());

INSERT INTO order_item (order_id, product_id, sku_id, quantity, unit_price, total_price) VALUES
('ORD_001', 'PROD_001', 'SKU_001', 1, 299.99, 299.99),
('ORD_002', 'PROD_002', 'SKU_002', 2, 299.99, 599.98);