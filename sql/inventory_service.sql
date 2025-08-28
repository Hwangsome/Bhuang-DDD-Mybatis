-- Inventory Service Database Schema

CREATE DATABASE IF NOT EXISTS ecommerce_inventory DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_inventory;

-- Inventory table
CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_id VARCHAR(64) NOT NULL UNIQUE COMMENT '库存ID',
    sku_id VARCHAR(64) NOT NULL COMMENT 'SKU ID',
    warehouse_id VARCHAR(64) NOT NULL COMMENT '仓库ID',
    available_quantity INT NOT NULL DEFAULT 0 COMMENT '可用库存数量',
    reserved_quantity INT NOT NULL DEFAULT 0 COMMENT '预留库存数量',
    total_quantity INT NOT NULL DEFAULT 0 COMMENT '总库存数量',
    status VARCHAR(32) NOT NULL COMMENT '库存状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_sku_id (sku_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_sku_warehouse (sku_id, warehouse_id),
    INDEX idx_status (status),
    INDEX idx_available_quantity (available_quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存表';

-- Inventory operation table
CREATE TABLE IF NOT EXISTS inventory_operation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation_id VARCHAR(64) NOT NULL UNIQUE COMMENT '操作ID',
    inventory_id VARCHAR(64) NOT NULL COMMENT '库存ID',
    operation_type VARCHAR(32) NOT NULL COMMENT '操作类型',
    quantity INT NOT NULL COMMENT '操作数量',
    reference_id VARCHAR(64) COMMENT '关联单据ID',
    reference_type VARCHAR(32) COMMENT '关联单据类型',
    description VARCHAR(255) COMMENT '操作描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_inventory_id (inventory_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_reference_id (reference_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存操作记录表';

-- Sample data
INSERT INTO inventory (inventory_id, sku_id, warehouse_id, available_quantity, reserved_quantity, total_quantity, status) VALUES
('INV_001', 'SKU_001', 'WH_001', 100, 10, 110, 'ACTIVE'),
('INV_002', 'SKU_002', 'WH_001', 50, 5, 55, 'ACTIVE'),
('INV_003', 'SKU_001', 'WH_002', 200, 20, 220, 'ACTIVE');

INSERT INTO inventory_operation (operation_id, inventory_id, operation_type, quantity, reference_id, reference_type, description) VALUES
('OP_001', 'INV_001', 'RESERVE', 1, 'ORD_001', 'ORDER', '订单预留库存'),
('OP_002', 'INV_002', 'RESERVE', 2, 'ORD_002', 'ORDER', '订单预留库存'),
('OP_003', 'INV_001', 'DEDUCT', 1, 'ORD_001', 'ORDER', '订单扣减库存');