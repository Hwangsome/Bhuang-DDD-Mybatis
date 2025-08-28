package com.ecommerce.product.domain.entity;

/**
 * 商品状态枚举
 * 领域概念：商品在其生命周期中的不同状态
 */
public enum ProductStatus {
    
    /**
     * 草稿状态 - 刚创建的商品，未发布
     */
    DRAFT("草稿"),
    
    /**
     * 活跃状态 - 已发布，可以销售
     */
    ACTIVE("活跃"),
    
    /**
     * 非活跃状态 - 已下架，不可销售
     */
    INACTIVE("下架"),
    
    /**
     * 已删除状态 - 软删除，不显示
     */
    DELETED("已删除");
    
    private final String description;
    
    ProductStatus(String description) {
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
     * 检查是否已发布
     */
    public boolean isPublished() {
        return this == ACTIVE || this == INACTIVE;
    }
    
    /**
     * 检查是否可以修改
     */
    public boolean canBeModified() {
        return this != DELETED;
    }
}