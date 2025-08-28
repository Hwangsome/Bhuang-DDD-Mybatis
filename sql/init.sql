-- ==================================================
-- 电商微服务数据库初始化脚本
-- 基于DDD设计，每个服务独立数据库
-- ==================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS ecommerce_user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ecommerce_product CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ecommerce_inventory CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ecommerce_order CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ecommerce_payment CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS ecommerce_notification CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ==================================================
-- 用户服务数据库表
-- ==================================================
USE ecommerce_user;

-- 用户表
CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    first_name VARCHAR(50) NOT NULL COMMENT '名',
    last_name VARCHAR(50) NOT NULL COMMENT '姓',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED') DEFAULT 'ACTIVE' COMMENT '用户状态',
    type ENUM('CUSTOMER', 'VIP_CUSTOMER', 'ADMIN', 'SUPER_ADMIN') DEFAULT 'CUSTOMER' COMMENT '用户类型',
    default_address JSON COMMENT '默认地址JSON',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(36) COMMENT '创建人',
    updated_by VARCHAR(36) COMMENT '更新人',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status (status),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB COMMENT='用户表';

-- ==================================================
-- 商品服务数据库表
-- ==================================================
USE ecommerce_product;

-- 商品分类表
CREATE TABLE categories (
    category_id VARCHAR(36) PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id VARCHAR(36) COMMENT '父分类ID',
    level INT NOT NULL DEFAULT 1 COMMENT '分类层级',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    status ENUM('ACTIVE', 'INACTIVE', 'DELETED') DEFAULT 'ACTIVE' COMMENT '分类状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_parent_id (parent_id),
    INDEX idx_level (level),
    INDEX idx_sort_order (sort_order),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='商品分类表';

-- 商品表
CREATE TABLE products (
    product_id VARCHAR(36) PRIMARY KEY COMMENT '商品ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    category_id VARCHAR(36) NOT NULL COMMENT '分类ID',
    image_urls JSON COMMENT '商品图片URL列表',
    detail_html LONGTEXT COMMENT '商品详情HTML',
    status ENUM('DRAFT', 'ACTIVE', 'INACTIVE', 'OUT_OF_STOCK', 'DELETED') DEFAULT 'DRAFT' COMMENT '商品状态',
    min_price DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '最低价格',
    max_price DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '最高价格',
    attributes JSON COMMENT '商品属性',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(36) COMMENT '创建人',
    updated_by VARCHAR(36) COMMENT '更新人',
    
    INDEX idx_name (name),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_price_range (min_price, max_price),
    INDEX idx_created_at (created_at),
    FULLTEXT idx_name_desc (name, description)
) ENGINE=InnoDB COMMENT='商品表';

-- SKU表
CREATE TABLE product_skus (
    sku_id VARCHAR(36) PRIMARY KEY COMMENT 'SKU ID',
    product_id VARCHAR(36) NOT NULL COMMENT '商品ID',
    sku_code VARCHAR(100) NOT NULL UNIQUE COMMENT 'SKU编码',
    specifications JSON COMMENT '规格列表',
    price DECIMAL(12,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(12,2) NOT NULL COMMENT '原价',
    image_url VARCHAR(500) COMMENT 'SKU图片URL',
    status ENUM('DRAFT', 'ACTIVE', 'INACTIVE', 'OUT_OF_STOCK', 'DELETED') DEFAULT 'DRAFT' COMMENT 'SKU状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_sku_code (sku_code),
    INDEX idx_status (status),
    INDEX idx_price (price),
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='SKU表';

-- ==================================================
-- 库存服务数据库表
-- ==================================================
USE ecommerce_inventory;

-- 库存表
CREATE TABLE inventories (
    sku_id VARCHAR(36) PRIMARY KEY COMMENT 'SKU ID',
    available_quantity BIGINT NOT NULL DEFAULT 0 COMMENT '可用数量',
    reserved_quantity BIGINT NOT NULL DEFAULT 0 COMMENT '预占数量',
    total_quantity BIGINT GENERATED ALWAYS AS (available_quantity + reserved_quantity) STORED COMMENT '总数量',
    safe_stock BIGINT NOT NULL DEFAULT 10 COMMENT '安全库存',
    max_stock BIGINT NOT NULL DEFAULT 10000 COMMENT '最大库存',
    stock_level ENUM('ABUNDANT', 'SUFFICIENT', 'LOW', 'VERY_LOW', 'OUT_OF_STOCK') COMMENT '库存级别',
    warehouse_code VARCHAR(20) NOT NULL DEFAULT 'MAIN' COMMENT '仓库编码',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_warehouse_code (warehouse_code),
    INDEX idx_stock_level (stock_level),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB COMMENT='库存表';

-- 库存变更记录表
CREATE TABLE stock_records (
    record_id VARCHAR(36) PRIMARY KEY COMMENT '记录ID',
    sku_id VARCHAR(36) NOT NULL COMMENT 'SKU ID',
    change_type ENUM('STOCK_IN', 'STOCK_OUT', 'STOCK_RESERVE', 'STOCK_RELEASE', 'STOCK_CONFIRM', 'STOCK_ADJUST', 'STOCK_CHECK') NOT NULL COMMENT '变更类型',
    change_quantity BIGINT NOT NULL COMMENT '变更数量',
    before_quantity BIGINT NOT NULL COMMENT '变更前数量',
    after_quantity BIGINT NOT NULL COMMENT '变更后数量',
    reason VARCHAR(500) COMMENT '变更原因',
    reference_id VARCHAR(36) COMMENT '关联ID',
    operator_id VARCHAR(36) COMMENT '操作人ID',
    warehouse_code VARCHAR(20) NOT NULL DEFAULT 'MAIN' COMMENT '仓库编码',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_sku_id (sku_id),
    INDEX idx_change_type (change_type),
    INDEX idx_reference_id (reference_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_warehouse_code (warehouse_code),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB COMMENT='库存变更记录表';

-- ==================================================
-- 订单服务数据库表
-- ==================================================
USE ecommerce_order;

-- 订单表
CREATE TABLE orders (
    order_id VARCHAR(36) PRIMARY KEY COMMENT '订单ID',
    order_number VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    status ENUM('ORDER_PENDING', 'ORDER_PAID', 'ORDER_CONFIRMED', 'ORDER_SHIPPED', 'ORDER_DELIVERED', 'ORDER_COMPLETED', 'ORDER_CANCELLED', 'ORDER_REFUNDED') DEFAULT 'ORDER_PENDING' COMMENT '订单状态',
    type ENUM('NORMAL_ORDER', 'PRE_ORDER', 'GROUP_ORDER', 'FLASH_SALE_ORDER') DEFAULT 'NORMAL_ORDER' COMMENT '订单类型',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
    product_amount DECIMAL(12,2) NOT NULL COMMENT '商品金额',
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '折扣金额',
    shipping_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '运费',
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '税费',
    shipping_address JSON NOT NULL COMMENT '收货地址',
    remark VARCHAR(500) COMMENT '订单备注',
    coupon_id VARCHAR(36) COMMENT '优惠券ID',
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    paid_time TIMESTAMP NULL COMMENT '付款时间',
    shipped_time TIMESTAMP NULL COMMENT '发货时间',
    delivered_time TIMESTAMP NULL COMMENT '送达时间',
    completed_time TIMESTAMP NULL COMMENT '完成时间',
    cancelled_time TIMESTAMP NULL COMMENT '取消时间',
    cancel_reason VARCHAR(500) COMMENT '取消原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(36) COMMENT '创建人',
    updated_by VARCHAR(36) COMMENT '更新人',
    
    INDEX idx_order_number (order_number),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_type (type),
    INDEX idx_order_time (order_time),
    INDEX idx_total_amount (total_amount),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB COMMENT='订单表';

-- 订单项表
CREATE TABLE order_items (
    order_item_id VARCHAR(36) PRIMARY KEY COMMENT '订单项ID',
    order_id VARCHAR(36) NOT NULL COMMENT '订单ID',
    sku_id VARCHAR(36) NOT NULL COMMENT 'SKU ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    sku_name VARCHAR(200) NOT NULL COMMENT 'SKU名称',
    image_url VARCHAR(500) COMMENT '商品图片',
    quantity INT NOT NULL COMMENT '购买数量',
    unit_price DECIMAL(12,2) NOT NULL COMMENT '单价',
    total_price DECIMAL(12,2) NOT NULL COMMENT '小计',
    original_price DECIMAL(12,2) NOT NULL COMMENT '原价',
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '折扣金额',
    sku_attributes JSON COMMENT 'SKU属性',
    
    INDEX idx_order_id (order_id),
    INDEX idx_sku_id (sku_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='订单项表';

-- ==================================================
-- 支付服务数据库表
-- ==================================================
USE ecommerce_payment;

-- 支付表
CREATE TABLE payments (
    payment_id VARCHAR(36) PRIMARY KEY COMMENT '支付ID',
    payment_number VARCHAR(32) NOT NULL UNIQUE COMMENT '支付单号',
    order_id VARCHAR(36) NOT NULL COMMENT '订单ID',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    payment_method ENUM('ALIPAY', 'WECHAT_PAY', 'UNION_PAY', 'CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'APPLE_PAY', 'GOOGLE_PAY') NOT NULL COMMENT '支付方式',
    status ENUM('PAYMENT_PENDING', 'PAYMENT_PROCESSING', 'PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'PAYMENT_CANCELLED', 'PAYMENT_REFUNDED', 'PAYMENT_PARTIAL_REFUNDED') DEFAULT 'PAYMENT_PENDING' COMMENT '支付状态',
    amount DECIMAL(12,2) NOT NULL COMMENT '支付金额',
    paid_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '已支付金额',
    refund_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '退款金额',
    third_party_transaction_id VARCHAR(64) COMMENT '第三方交易ID',
    third_party_payment_id VARCHAR(64) COMMENT '第三方支付ID',
    payment_details JSON COMMENT '支付详情',
    payment_time TIMESTAMP NULL COMMENT '支付时间',
    expire_time TIMESTAMP NULL COMMENT '支付过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(36) COMMENT '创建人',
    updated_by VARCHAR(36) COMMENT '更新人',
    
    INDEX idx_payment_number (payment_number),
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_payment_method (payment_method),
    INDEX idx_payment_time (payment_time),
    INDEX idx_expire_time (expire_time),
    INDEX idx_third_party_transaction_id (third_party_transaction_id)
) ENGINE=InnoDB COMMENT='支付表';

-- 退款表
CREATE TABLE refunds (
    refund_id VARCHAR(36) PRIMARY KEY COMMENT '退款ID',
    refund_number VARCHAR(32) NOT NULL UNIQUE COMMENT '退款单号',
    payment_id VARCHAR(36) NOT NULL COMMENT '支付ID',
    order_id VARCHAR(36) NOT NULL COMMENT '订单ID',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    status ENUM('REFUND_PENDING', 'REFUND_PROCESSING', 'REFUND_SUCCESS', 'REFUND_FAILED', 'REFUND_REJECTED') DEFAULT 'REFUND_PENDING' COMMENT '退款状态',
    refund_amount DECIMAL(12,2) NOT NULL COMMENT '退款金额',
    refund_reason VARCHAR(500) COMMENT '退款原因',
    third_party_refund_id VARCHAR(64) COMMENT '第三方退款ID',
    refund_details JSON COMMENT '退款详情',
    refund_time TIMESTAMP NULL COMMENT '退款时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(36) COMMENT '创建人',
    updated_by VARCHAR(36) COMMENT '更新人',
    
    INDEX idx_refund_number (refund_number),
    INDEX idx_payment_id (payment_id),
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_refund_time (refund_time),
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id)
) ENGINE=InnoDB COMMENT='退款表';

-- 支付记录表
CREATE TABLE payment_records (
    record_id VARCHAR(36) PRIMARY KEY COMMENT '记录ID',
    payment_id VARCHAR(36) NOT NULL COMMENT '支付ID',
    record_type ENUM('PAYMENT_CREATED', 'PAYMENT_STARTED', 'PAYMENT_COMPLETED', 'PAYMENT_FAILED_RECORD', 'REFUND_STARTED', 'REFUND_COMPLETED', 'CALLBACK_RECEIVED') NOT NULL COMMENT '记录类型',
    status ENUM('PAYMENT_PENDING', 'PAYMENT_PROCESSING', 'PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'PAYMENT_CANCELLED', 'PAYMENT_REFUNDED', 'PAYMENT_PARTIAL_REFUNDED') COMMENT '状态',
    message TEXT COMMENT '消息内容',
    third_party_response TEXT COMMENT '第三方响应',
    metadata JSON COMMENT '元数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_payment_id (payment_id),
    INDEX idx_record_type (record_type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id)
) ENGINE=InnoDB COMMENT='支付记录表';

-- ==================================================
-- 通知服务数据库表
-- ==================================================
USE ecommerce_notification;

-- 通知表
CREATE TABLE notifications (
    notification_id VARCHAR(36) PRIMARY KEY COMMENT '通知ID',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    type ENUM('USER_REGISTRATION', 'ORDER_CREATED', 'ORDER_PAID', 'ORDER_SHIPPED', 'ORDER_DELIVERED', 'ORDER_CANCELLED', 'PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'REFUND_PROCESSED', 'STOCK_ALERT', 'SYSTEM_MAINTENANCE', 'PROMOTIONAL_OFFER', 'PASSWORD_RESET', 'SECURITY_ALERT') NOT NULL COMMENT '通知类型',
    channel ENUM('EMAIL', 'SMS', 'PUSH', 'IN_APP', 'WECHAT', 'WEBHOOK') NOT NULL COMMENT '通知渠道',
    status ENUM('NOTIFICATION_PENDING', 'NOTIFICATION_SENT', 'NOTIFICATION_DELIVERED', 'NOTIFICATION_FAILED', 'NOTIFICATION_READ') DEFAULT 'NOTIFICATION_PENDING' COMMENT '通知状态',
    priority ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL' COMMENT '通知优先级',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    template_id VARCHAR(36) COMMENT '模板ID',
    template_params JSON COMMENT '模板参数',
    recipient VARCHAR(200) NOT NULL COMMENT '接收人',
    sender VARCHAR(200) COMMENT '发送人',
    reference_id VARCHAR(36) COMMENT '关联业务ID',
    reference_type VARCHAR(50) COMMENT '关联业务类型',
    metadata JSON COMMENT '元数据',
    sent_time TIMESTAMP NULL COMMENT '发送时间',
    delivered_time TIMESTAMP NULL COMMENT '送达时间',
    read_time TIMESTAMP NULL COMMENT '读取时间',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    error_message TEXT COMMENT '错误信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_channel (channel),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_reference_id (reference_id),
    INDEX idx_reference_type (reference_type),
    INDEX idx_sent_time (sent_time),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB COMMENT='通知表';

-- 通知模板表
CREATE TABLE notification_templates (
    template_id VARCHAR(36) PRIMARY KEY COMMENT '模板ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    type ENUM('USER_REGISTRATION', 'ORDER_CREATED', 'ORDER_PAID', 'ORDER_SHIPPED', 'ORDER_DELIVERED', 'ORDER_CANCELLED', 'PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'REFUND_PROCESSED', 'STOCK_ALERT', 'SYSTEM_MAINTENANCE', 'PROMOTIONAL_OFFER', 'PASSWORD_RESET', 'SECURITY_ALERT') NOT NULL COMMENT '通知类型',
    channel ENUM('EMAIL', 'SMS', 'PUSH', 'IN_APP', 'WECHAT', 'WEBHOOK') NOT NULL COMMENT '通知渠道',
    title_template VARCHAR(500) NOT NULL COMMENT '标题模板',
    content_template TEXT NOT NULL COMMENT '内容模板',
    required_params JSON COMMENT '必需参数列表',
    default_params JSON COMMENT '默认参数',
    active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_type (type),
    INDEX idx_channel (channel),
    INDEX idx_active (active),
    UNIQUE KEY uk_type_channel (type, channel)
) ENGINE=InnoDB COMMENT='通知模板表';

-- 用户通知设置表
CREATE TABLE user_notification_settings (
    user_id VARCHAR(36) PRIMARY KEY COMMENT '用户ID',
    type_settings JSON COMMENT '通知类型设置',
    channel_settings JSON COMMENT '通知渠道设置',
    global_enabled BOOLEAN DEFAULT TRUE COMMENT '全局开关',
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区设置',
    quiet_start_hour INT DEFAULT 22 COMMENT '免打扰开始时间(小时)',
    quiet_end_hour INT DEFAULT 8 COMMENT '免打扰结束时间(小时)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='用户通知设置表';