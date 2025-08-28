package com.ecommerce.product.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * SKU标识符 - 值对象
 * 领域概念：商品库存单位的唯一标识符
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
    
    /**
     * 生成新的SKU ID
     */
    public static SkuId generate() {
        return new SkuId("SKU-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
    }
    
    /**
     * 从商品ID和规格生成SKU ID
     */
    public static SkuId generateFromProduct(ProductId productId, String specification) {
        if (specification == null || specification.trim().isEmpty()) {
            return new SkuId(productId.getValue() + "-DEFAULT");
        }
        
        String cleanSpec = specification.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        return new SkuId(productId.getValue() + "-" + cleanSpec);
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
    
    /**
     * 获取规格部分
     */
    public String getSpecificationPart() {
        int dashIndex = value.lastIndexOf("-");
        return dashIndex > 0 && dashIndex < value.length() - 1 ? value.substring(dashIndex + 1) : "";
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