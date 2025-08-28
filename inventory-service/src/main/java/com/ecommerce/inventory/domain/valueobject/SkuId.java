package com.ecommerce.inventory.domain.valueobject;

import java.util.Objects;

/**
 * SKU标识符 - 值对象（库存服务使用）
 * 领域概念：引用商品服务的SKU标识符
 * 特性：不可变、具有业务含义、封装验证逻辑
 */
public final class SkuId {
    
    private final String value;
    
    private SkuId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU ID不能为空");
        }
        
        if (value.length() < 3 || value.length() > 50) {
            throw new IllegalArgumentException("SKU ID长度必须在3-50个字符之间");
        }
        
        // SKU ID格式验证：允许字母、数字、短横线和下划线
        if (!value.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("SKU ID只能包含字母、数字、短横线和下划线");
        }
        
        this.value = value;
    }
    
    /**
     * 创建SKU ID
     */
    public static SkuId of(String value) {
        return new SkuId(value);
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 检查是否为默认SKU
     */
    public boolean isDefault() {
        return value.endsWith("-DEFAULT");
    }
    
    /**
     * 获取商品ID部分
     */
    public String getProductPart() {
        int dashIndex = value.lastIndexOf("-");
        return dashIndex > 0 ? value.substring(0, dashIndex) : value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkuId skuId = (SkuId) o;
        return Objects.equals(value, skuId.value);
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