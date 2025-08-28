package com.ecommerce.product.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * 商品标识符 - 值对象
 * 领域概念：商品的唯一标识符
 * 特性：不可变、具有业务含义、封装验证逻辑
 */
public final class ProductId {
    
    private final String value;
    
    private ProductId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        if (value.length() < 3 || value.length() > 50) {
            throw new IllegalArgumentException("商品ID长度必须在3-50个字符之间");
        }
        
        // 商品ID格式验证：允许字母、数字、短横线
        if (!value.matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("商品ID只能包含字母、数字和短横线");
        }
        
        this.value = value;
    }
    
    /**
     * 创建商品ID
     */
    public static ProductId of(String value) {
        return new ProductId(value);
    }
    
    /**
     * 生成新的商品ID
     */
    public static ProductId generate() {
        return new ProductId("PROD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
    }
    
    /**
     * 根据分类生成商品ID
     */
    public static ProductId generateWithCategory(String categoryCode) {
        if (categoryCode == null || categoryCode.trim().isEmpty()) {
            return generate();
        }
        
        String cleanCode = categoryCode.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return new ProductId(cleanCode + "-" + randomPart);
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 检查是否为生成的ID格式
     */
    public boolean isGenerated() {
        return value.matches("^[A-Z]+-[A-Z0-9]+$");
    }
    
    /**
     * 获取分类前缀
     */
    public String getCategoryPrefix() {
        int dashIndex = value.indexOf("-");
        return dashIndex > 0 ? value.substring(0, dashIndex) : "";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductId productId = (ProductId) o;
        return Objects.equals(value, productId.value);
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