-- 用户服务数据库结构
-- 设计原则：DDD值对象分拆存储，支持乐观锁和软删除

CREATE DATABASE IF NOT EXISTS ecommerce_user CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ecommerce_user;

-- 用户表
CREATE TABLE users (
    -- 主键和业务标识
    user_id VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '用户ID（UUID）',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    
    -- 联系方式（值对象分拆存储）
    email VARCHAR(254) NOT NULL UNIQUE COMMENT '邮箱地址',
    phone VARCHAR(20) NOT NULL COMMENT '手机号码',
    phone_country_code VARCHAR(5) NOT NULL DEFAULT '+86' COMMENT '手机号国家代码',
    
    -- 基础信息
    first_name VARCHAR(50) NOT NULL COMMENT '名',
    last_name VARCHAR(50) NOT NULL COMMENT '姓', 
    avatar_url VARCHAR(500) NULL COMMENT '头像URL',
    
    -- 状态和类型枚举
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态：ACTIVE, INACTIVE, SUSPENDED, DELETED',
    type VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER' COMMENT '用户类型：CUSTOMER, VIP_CUSTOMER, ADMIN, SUPER_ADMIN',
    
    -- 默认地址（值对象分拆存储）
    address_country VARCHAR(50) NULL COMMENT '地址-国家',
    address_province VARCHAR(50) NULL COMMENT '地址-省份',
    address_city VARCHAR(50) NULL COMMENT '地址-城市',
    address_district VARCHAR(50) NULL COMMENT '地址-区域',
    address_street VARCHAR(200) NULL COMMENT '地址-街道',
    address_postal_code VARCHAR(20) NULL COMMENT '地址-邮政编码',
    address_contact_name VARCHAR(50) NULL COMMENT '地址-联系人姓名',
    address_contact_phone VARCHAR(20) NULL COMMENT '地址-联系人电话',
    
    -- 审计字段
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(64) NULL COMMENT '创建人ID',
    updated_by VARCHAR(64) NULL COMMENT '更新人ID',
    
    -- DDD支持：乐观锁和软删除
    version INT NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '软删除标记：0-未删除，1-已删除',
    
    -- 索引约束
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status (status),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at),
    INDEX idx_address_location (address_province, address_city),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 插入测试数据
INSERT INTO users (
    user_id, username, email, phone, phone_country_code,
    first_name, last_name, status, type,
    address_country, address_province, address_city, address_street,
    address_contact_name, address_contact_phone
) VALUES 
(
    'user_001', 'john_doe', 'john.doe@example.com', '13800138001', '+86',
    'John', 'Doe', 'ACTIVE', 'CUSTOMER',
    '中国', '北京', '北京市', '朝阳区三里屯路123号',
    '约翰·多伊', '13800138001'
),
(
    'user_002', 'jane_smith', 'jane.smith@example.com', '13800138002', '+86', 
    'Jane', 'Smith', 'ACTIVE', 'VIP_CUSTOMER',
    '中国', '上海', '上海市', '浦东新区陆家嘴金融区456号',
    '简·史密斯', '13800138002'
),
(
    'admin_001', 'admin', 'admin@ecommerce.com', '13800138888', '+86',
    'System', 'Admin', 'ACTIVE', 'SUPER_ADMIN', 
    '中国', '北京', '北京市', '海淀区中关村大街1号',
    '系统管理员', '13800138888'
);

-- 创建用户唯一约束（支持软删除）
-- 在MySQL中，NULL值不参与唯一约束，所以我们需要函数索引或者触发器
ALTER TABLE users ADD CONSTRAINT uk_username_not_deleted 
    UNIQUE (username, deleted);
    
ALTER TABLE users ADD CONSTRAINT uk_email_not_deleted 
    UNIQUE (email, deleted);
    
ALTER TABLE users ADD CONSTRAINT uk_phone_not_deleted 
    UNIQUE (phone, deleted);

-- 检查约束（MySQL 8.0+支持）
ALTER TABLE users ADD CONSTRAINT chk_status 
    CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'DELETED'));
    
ALTER TABLE users ADD CONSTRAINT chk_type 
    CHECK (type IN ('CUSTOMER', 'VIP_CUSTOMER', 'ADMIN', 'SUPER_ADMIN'));
    
ALTER TABLE users ADD CONSTRAINT chk_deleted 
    CHECK (deleted IN (0, 1));

-- 统计信息查询视图
CREATE VIEW user_statistics AS
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) as active_users,
    COUNT(CASE WHEN status = 'INACTIVE' THEN 1 END) as inactive_users,
    COUNT(CASE WHEN status = 'SUSPENDED' THEN 1 END) as suspended_users,
    COUNT(CASE WHEN type = 'CUSTOMER' THEN 1 END) as regular_customers,
    COUNT(CASE WHEN type = 'VIP_CUSTOMER' THEN 1 END) as vip_customers,
    COUNT(CASE WHEN type = 'ADMIN' THEN 1 END) as admin_users,
    COUNT(CASE WHEN deleted = 0 THEN 1 END) as not_deleted_users,
    COUNT(CASE WHEN deleted = 1 THEN 1 END) as deleted_users
FROM users;

-- 性能优化：复合索引
CREATE INDEX idx_status_type_created ON users(status, type, created_at DESC);
CREATE INDEX idx_email_deleted ON users(email, deleted);
CREATE INDEX idx_phone_deleted ON users(phone, deleted);

-- 全文搜索索引（用户名称搜索）
ALTER TABLE users ADD FULLTEXT(first_name, last_name);

-- 验证数据
SELECT 'User service database initialized successfully' as result;
SELECT COUNT(*) as user_count FROM users WHERE deleted = 0;