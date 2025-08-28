package com.ecommerce.order.domain.valueobject;

import java.util.Objects;

/**
 * 用户标识符 - 值对象（订单服务使用）
 * 领域概念：引用用户服务的用户标识符
 * 特性：不可变、具有业务含义、封装验证逻辑
 */
public final class UserId {
    
    private final String value;
    
    private UserId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        if (value.length() < 3 || value.length() > 50) {
            throw new IllegalArgumentException("用户ID长度必须在3-50个字符之间");
        }
        
        // 用户ID格式验证：允许字母、数字、短横线和下划线
        if (!value.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("用户ID只能包含字母、数字、短横线和下划线");
        }
        
        this.value = value;
    }
    
    /**
     * 创建用户ID
     */
    public static UserId of(String value) {
        return new UserId(value);
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 检查是否为系统用户
     */
    public boolean isSystemUser() {
        return value.startsWith("SYS_");
    }
    
    /**
     * 检查是否为游客用户
     */
    public boolean isGuestUser() {
        return value.startsWith("GUEST_");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
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