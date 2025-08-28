package com.ecommerce.order.domain.valueobject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * 订单标识符 - 值对象
 * 领域概念：订单的唯一标识符，包含业务语义
 * 特性：不可变、具有时间特征、便于追踪
 */
public final class OrderId {
    
    private final String value;
    
    private OrderId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        
        if (value.length() < 8 || value.length() > 50) {
            throw new IllegalArgumentException("订单ID长度必须在8-50个字符之间");
        }
        
        // 订单ID格式验证：允许字母、数字、短横线
        if (!value.matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("订单ID只能包含字母、数字和短横线");
        }
        
        this.value = value;
    }
    
    /**
     * 创建订单ID
     */
    public static OrderId of(String value) {
        return new OrderId(value);
    }
    
    /**
     * 生成新的订单ID（基于时间戳）
     */
    public static OrderId generate() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return new OrderId("ORD-" + timestamp + "-" + randomSuffix);
    }
    
    /**
     * 根据用户ID生成订单ID
     */
    public static OrderId generateForUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return generate();
        }
        
        String userPrefix = userId.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (userPrefix.length() > 8) {
            userPrefix = userPrefix.substring(0, 8);
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
        
        return new OrderId("ORD-" + userPrefix + "-" + timestamp + "-" + randomSuffix);
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 检查是否为生成的订单ID格式
     */
    public boolean isGenerated() {
        return value.startsWith("ORD-");
    }
    
    /**
     * 提取订单创建日期（如果是生成的ID）
     */
    public String getDatePart() {
        if (!isGenerated()) {
            return null;
        }
        
        String[] parts = value.split("-");
        if (parts.length >= 2) {
            String datePart = parts[1];
            if (datePart.length() >= 8) {
                // 尝试提取日期部分 yyyyMMdd
                return datePart.substring(0, 8);
            }
        }
        
        return null;
    }
    
    /**
     * 提取用户前缀（如果包含）
     */
    public String getUserPrefix() {
        if (!isGenerated()) {
            return null;
        }
        
        String[] parts = value.split("-");
        if (parts.length >= 4) {
            // 格式: ORD-USER-TIMESTAMP-RANDOM
            return parts[1];
        }
        
        return null;
    }
    
    /**
     * 生成简短显示格式
     */
    public String getDisplayFormat() {
        if (value.length() <= 12) {
            return value;
        }
        
        // 显示前8位和后4位
        return value.substring(0, 8) + "..." + value.substring(value.length() - 4);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}