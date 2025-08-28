package com.ecommerce.product.domain.entity;

/**
 * SKU状态枚举
 * 领域概念：SKU在其生命周期中的不同状态
 */
public enum SkuStatus {
    
    /**
     * 活跃状态 - 可以销售
     */
    ACTIVE("活跃"),
    
    /**
     * 非活跃状态 - 暂停销售
     */
    INACTIVE("非活跃"),
    
    /**
     * 已删除状态 - 软删除，不显示
     */
    DELETED("已删除");
    
    private final String description;
    
    SkuStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否可以销售
     */
    public boolean canBeSold() {
        return this == ACTIVE;
    }
    
    /**
     * 检查是否可以修改
     */
    public boolean canBeModified() {
        return this != DELETED;
    }
}