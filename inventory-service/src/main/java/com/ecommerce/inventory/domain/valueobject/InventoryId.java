package com.ecommerce.inventory.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * 库存标识符 - 值对象
 * 领域概念：库存记录的唯一标识符
 * 特性：不可变、具有业务含义、封装验证逻辑
 */
public final class InventoryId {
    
    private final String value;
    
    private InventoryId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("库存ID不能为空");
        }
        
        if (value.length() < 3 || value.length() > 50) {
            throw new IllegalArgumentException("库存ID长度必须在3-50个字符之间");
        }
        
        // 库存ID格式验证：允许字母、数字、短横线
        if (!value.matches("^[a-zA-Z0-9-]+$")) {
            throw new IllegalArgumentException("库存ID只能包含字母、数字和短横线");
        }
        
        this.value = value;
    }
    
    /**
     * 创建库存ID
     */
    public static InventoryId of(String value) {
        return new InventoryId(value);
    }
    
    /**
     * 生成新的库存ID
     */
    public static InventoryId generate() {
        return new InventoryId("INV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
    }
    
    /**
     * 根据SKU ID生成库存ID
     */
    public static InventoryId generateFromSku(String skuId) {
        if (skuId == null || skuId.trim().isEmpty()) {
            return generate();
        }
        
        String cleanSkuId = skuId.replaceAll("[^a-zA-Z0-9]", "");
        return new InventoryId("INV-" + cleanSkuId);
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 检查是否为生成的ID格式
     */
    public boolean isGenerated() {
        return value.startsWith("INV-");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryId that = (InventoryId) o;
        return Objects.equals(value, that.value);
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