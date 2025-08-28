package com.ecommerce.inventory.domain.entity;

/**
 * 库存操作类型枚举
 * 领域概念：定义各种库存操作的类型，用于记录和审计
 */
public enum InventoryOperationType {
    
    /**
     * 入库操作
     */
    STOCK_IN("入库"),
    
    /**
     * 出库操作
     */
    STOCK_OUT("出库"),
    
    /**
     * 预留库存
     */
    RESERVE("预留"),
    
    /**
     * 释放预留
     */
    RELEASE_RESERVATION("释放预留"),
    
    /**
     * 确认预留（转为实际出库）
     */
    CONFIRM_RESERVATION("确认预留"),
    
    /**
     * 冻结库存
     */
    FREEZE("冻结"),
    
    /**
     * 解冻库存
     */
    UNFREEZE("解冻"),
    
    /**
     * 库存调增（盘点等）
     */
    ADJUST_INCREASE("调增"),
    
    /**
     * 库存调减（盘点等）
     */
    ADJUST_DECREASE("调减"),
    
    /**
     * 库存转移
     */
    TRANSFER("转移");
    
    private final String description;
    
    InventoryOperationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否为增加库存的操作
     */
    public boolean isIncreaseOperation() {
        return this == STOCK_IN || this == RELEASE_RESERVATION || this == UNFREEZE || this == ADJUST_INCREASE;
    }
    
    /**
     * 检查是否为减少库存的操作
     */
    public boolean isDecreaseOperation() {
        return this == STOCK_OUT || this == RESERVE || this == CONFIRM_RESERVATION || this == FREEZE || this == ADJUST_DECREASE;
    }
    
    /**
     * 检查是否为预留相关操作
     */
    public boolean isReservationOperation() {
        return this == RESERVE || this == RELEASE_RESERVATION || this == CONFIRM_RESERVATION;
    }
    
    /**
     * 检查是否为冻结相关操作
     */
    public boolean isFreezeOperation() {
        return this == FREEZE || this == UNFREEZE;
    }
    
    /**
     * 检查是否为调整操作
     */
    public boolean isAdjustmentOperation() {
        return this == ADJUST_INCREASE || this == ADJUST_DECREASE;
    }
}