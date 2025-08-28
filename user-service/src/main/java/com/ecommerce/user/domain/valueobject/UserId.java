package com.ecommerce.user.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * 用户ID值对象 - 强类型ID避免原始类型传递错误
 * 体现DDD价值对象的不可变性和业务语义
 */
public final class UserId {
    
    private final String value;

    private UserId(String value) {
        this.value = Objects.requireNonNull(value, "User ID不能为空");
        validateFormat(value);
    }

    /**
     * 创建新的用户ID
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    /**
     * 从字符串创建用户ID
     */
    public static UserId of(String id) {
        return new UserId(id);
    }

    /**
     * 验证ID格式
     */
    private void validateFormat(String value) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID不能为空字符串");
        }
        // 可以添加更多格式验证规则
    }

    public String getValue() {
        return value;
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
        return "UserId{" +
                "value='" + value + '\'' +
                '}';
    }
}