package com.ecommerce.inventory.domain.entity;

/**
 * 库存状态枚举
 * 领域概念：库存记录在其生命周期中的不同状态
 */
public enum InventoryStatus {
    
    /**
     * 活跃状态 - 正常库存管理状态
     */
    ACTIVE("活跃"),
    
    /**
     * 非活跃状态 - 暂停库存管理
     */
    INACTIVE("非活跃"),
    
    /**
     * 已删除状态 - 软删除，不显示
     */
    DELETED("已删除");
    
    private final String description;
    
    InventoryStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否可以进行库存操作
     */
    public boolean canOperate() {
        return this == ACTIVE;
    }
    
    /**
     * 检查是否可以修改
     */
    public boolean canBeModified() {
        return this != DELETED;
    }
}