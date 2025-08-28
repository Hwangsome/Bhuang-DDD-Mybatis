package com.ecommerce.inventory.domain.entity;

import com.ecommerce.inventory.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 库存 - 聚合根
 * 领域概念：商品SKU在特定仓库的库存记录
 * 职责：库存数量管理、库存操作记录、库存状态控制
 */
public class Inventory {
    
    private InventoryId id;
    private SkuId skuId;
    private WarehouseId warehouseId;
    private Quantity totalQuantity;        // 总库存
    private Quantity availableQuantity;    // 可用库存
    private Quantity reservedQuantity;     // 预留库存
    private Quantity frozenQuantity;       // 冻结库存
    private Quantity safetyStockQuantity;  // 安全库存
    private InventoryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;                  // 乐观锁版本号
    
    // 私有构造函数，强制使用工厂方法
    private Inventory() {
        this.status = InventoryStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0L;
    }
    
    /**
     * 创建新库存记录
     */
    public static Inventory create(SkuId skuId, WarehouseId warehouseId, Quantity initialQuantity, 
                                 Quantity safetyStockQuantity) {
        if (skuId == null) {
            throw new IllegalArgumentException("SKU ID不能为空");
        }
        
        if (warehouseId == null) {
            throw new IllegalArgumentException("仓库ID不能为空");
        }
        
        if (initialQuantity == null) {
            initialQuantity = Quantity.zero();
        }
        
        if (safetyStockQuantity == null) {
            safetyStockQuantity = Quantity.zero();
        }
        
        Inventory inventory = new Inventory();
        inventory.id = InventoryId.generateFromSku(skuId.getValue());
        inventory.skuId = skuId;
        inventory.warehouseId = warehouseId;
        inventory.totalQuantity = initialQuantity;
        inventory.availableQuantity = initialQuantity;
        inventory.reservedQuantity = Quantity.zero();
        inventory.frozenQuantity = Quantity.zero();
        inventory.safetyStockQuantity = safetyStockQuantity;
        
        return inventory;
    }
    
    /**
     * 从持久化数据重建库存
     */
    public static Inventory restore(InventoryId id, SkuId skuId, WarehouseId warehouseId,
                                  Quantity totalQuantity, Quantity availableQuantity,
                                  Quantity reservedQuantity, Quantity frozenQuantity,
                                  Quantity safetyStockQuantity, InventoryStatus status,
                                  LocalDateTime createdAt, LocalDateTime updatedAt, Long version) {
        Inventory inventory = new Inventory();
        inventory.id = id;
        inventory.skuId = skuId;
        inventory.warehouseId = warehouseId;
        inventory.totalQuantity = totalQuantity != null ? totalQuantity : Quantity.zero();
        inventory.availableQuantity = availableQuantity != null ? availableQuantity : Quantity.zero();
        inventory.reservedQuantity = reservedQuantity != null ? reservedQuantity : Quantity.zero();
        inventory.frozenQuantity = frozenQuantity != null ? frozenQuantity : Quantity.zero();
        inventory.safetyStockQuantity = safetyStockQuantity != null ? safetyStockQuantity : Quantity.zero();
        inventory.status = status != null ? status : InventoryStatus.ACTIVE;
        inventory.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        inventory.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        inventory.version = version != null ? version : 0L;
        
        return inventory;
    }
    
    /**
     * 入库操作
     */
    public void stockIn(Quantity quantity, String reason) {
        if (status != InventoryStatus.ACTIVE) {
            throw new IllegalStateException("库存状态不活跃，不能执行入库操作");
        }
        
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("入库数量必须大于0");
        }
        
        this.totalQuantity = this.totalQuantity.add(quantity);
        this.availableQuantity = this.availableQuantity.add(quantity);
        this.updatedAt = LocalDateTime.now();
        
        // 这里可以记录库存操作日志
        recordInventoryOperation(InventoryOperationType.STOCK_IN, quantity, reason);
    }
    
    /**
     * 出库操作
     */
    public void stockOut(Quantity quantity, String reason) {
        if (status != InventoryStatus.ACTIVE) {
            throw new IllegalStateException("库存状态不活跃，不能执行出库操作");
        }
        
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("出库数量必须大于0");
        }
        
        if (availableQuantity.lessThan(quantity)) {
            throw new IllegalStateException("可用库存不足，无法执行出库操作");
        }
        
        this.totalQuantity = this.totalQuantity.subtract(quantity);
        this.availableQuantity = this.availableQuantity.subtract(quantity);
        this.updatedAt = LocalDateTime.now();
        
        recordInventoryOperation(InventoryOperationType.STOCK_OUT, quantity, reason);
    }
    
    /**
     * 预留库存
     */
    public void reserve(Quantity quantity, String reason) {
        if (status != InventoryStatus.ACTIVE) {
            throw new IllegalStateException("库存状态不活跃，不能执行预留操作");
        }
        
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("预留数量必须大于0");
        }
        
        if (availableQuantity.lessThan(quantity)) {
            throw new IllegalStateException("可用库存不足，无法执行预留操作");
        }
        
        this.availableQuantity = this.availableQuantity.subtract(quantity);
        this.reservedQuantity = this.reservedQuantity.add(quantity);
        this.updatedAt = LocalDateTime.now();
        
        recordInventoryOperation(InventoryOperationType.RESERVE, quantity, reason);
    }
    
    /**
     * 释放预留库存
     */
    public void releaseReservation(Quantity quantity, String reason) {
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("释放数量必须大于0");
        }
        
        if (reservedQuantity.lessThan(quantity)) {
            throw new IllegalStateException("预留库存不足，无法释放指定数量");
        }
        
        this.reservedQuantity = this.reservedQuantity.subtract(quantity);
        this.availableQuantity = this.availableQuantity.add(quantity);
        this.updatedAt = LocalDateTime.now();
        
        recordInventoryOperation(InventoryOperationType.RELEASE_RESERVATION, quantity, reason);
    }
    
    /**
     * 确认预留（将预留库存转为实际出库）
     */
    public void confirmReservation(Quantity quantity, String reason) {
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("确认数量必须大于0");
        }
        
        if (reservedQuantity.lessThan(quantity)) {
            throw new IllegalStateException("预留库存不足，无法确认指定数量");
        }
        
        this.reservedQuantity = this.reservedQuantity.subtract(quantity);
        this.totalQuantity = this.totalQuantity.subtract(quantity);
        this.updatedAt = LocalDateTime.now();
        
        recordInventoryOperation(InventoryOperationType.CONFIRM_RESERVATION, quantity, reason);
    }
    
    /**
     * 冻结库存
     */
    public void freeze(Quantity quantity, String reason) {
        if (status != InventoryStatus.ACTIVE) {
            throw new IllegalStateException("库存状态不活跃，不能执行冻结操作");
        }
        
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("冻结数量必须大于0");
        }
        
        if (availableQuantity.lessThan(quantity)) {
            throw new IllegalStateException("可用库存不足，无法执行冻结操作");
        }
        
        this.availableQuantity = this.availableQuantity.subtract(quantity);
        this.frozenQuantity = this.frozenQuantity.add(quantity);
        this.updatedAt = LocalDateTime.now();
        
        recordInventoryOperation(InventoryOperationType.FREEZE, quantity, reason);
    }
    
    /**
     * 解冻库存
     */
    public void unfreeze(Quantity quantity, String reason) {
        if (quantity == null || !quantity.isPositive()) {
            throw new IllegalArgumentException("解冻数量必须大于0");
        }
        
        if (frozenQuantity.lessThan(quantity)) {
            throw new IllegalStateException("冻结库存不足，无法解冻指定数量");
        }
        
        this.frozenQuantity = this.frozenQuantity.subtract(quantity);
        this.availableQuantity = this.availableQuantity.add(quantity);
        this.updatedAt = LocalDateTime.now();
        
        recordInventoryOperation(InventoryOperationType.UNFREEZE, quantity, reason);
    }
    
    /**
     * 调整库存（盘点等场景）
     */
    public void adjust(Quantity newTotalQuantity, String reason) {
        if (status != InventoryStatus.ACTIVE) {
            throw new IllegalStateException("库存状态不活跃，不能执行调整操作");
        }
        
        if (newTotalQuantity == null) {
            throw new IllegalArgumentException("新的总库存数量不能为空");
        }
        
        // 计算调整数量
        Quantity adjustQuantity;
        InventoryOperationType operationType;
        
        if (newTotalQuantity.greaterThan(totalQuantity)) {
            adjustQuantity = newTotalQuantity.subtract(totalQuantity);
            operationType = InventoryOperationType.ADJUST_INCREASE;
        } else if (newTotalQuantity.lessThan(totalQuantity)) {
            adjustQuantity = totalQuantity.subtract(newTotalQuantity);
            operationType = InventoryOperationType.ADJUST_DECREASE;
        } else {
            return; // 数量无变化
        }
        
        // 计算新的可用数量
        Quantity occupiedQuantity = reservedQuantity.add(frozenQuantity);
        if (newTotalQuantity.lessThan(occupiedQuantity)) {
            throw new IllegalStateException("调整后的库存不能少于已占用的库存数量");
        }
        
        this.totalQuantity = newTotalQuantity;
        this.availableQuantity = newTotalQuantity.subtract(occupiedQuantity);
        this.updatedAt = LocalDateTime.now();
        
        recordInventoryOperation(operationType, adjustQuantity, reason);
    }
    
    /**
     * 更新安全库存
     */
    public void updateSafetyStock(Quantity newSafetyStockQuantity) {
        if (newSafetyStockQuantity == null) {
            newSafetyStockQuantity = Quantity.zero();
        }
        
        this.safetyStockQuantity = newSafetyStockQuantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查库存是否充足
     */
    public boolean isSufficient(Quantity requiredQuantity) {
        return availableQuantity.greaterThanOrEqual(requiredQuantity);
    }
    
    /**
     * 检查是否低于安全库存
     */
    public boolean isBelowSafetyStock() {
        return totalQuantity.lessThan(safetyStockQuantity);
    }
    
    /**
     * 检查是否缺货
     */
    public boolean isOutOfStock() {
        return availableQuantity.isZero();
    }
    
    /**
     * 激活库存
     */
    public void activate() {
        this.status = InventoryStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 停用库存
     */
    public void deactivate() {
        this.status = InventoryStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 删除库存
     */
    public void delete() {
        if (!totalQuantity.isZero()) {
            throw new IllegalStateException("库存数量不为零，不能删除");
        }
        
        this.status = InventoryStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 验证库存数据一致性
     */
    public void validateConsistency() {
        Quantity calculatedAvailable = totalQuantity.subtract(reservedQuantity).subtract(frozenQuantity);
        
        if (!availableQuantity.equals(calculatedAvailable)) {
            throw new IllegalStateException("库存数据不一致：可用库存与计算结果不符");
        }
    }
    
    /**
     * 记录库存操作（留给基础设施层实现）
     */
    private void recordInventoryOperation(InventoryOperationType operationType, Quantity quantity, String reason) {
        // 这里可以发布领域事件，由基础设施层记录操作日志
        // 暂时留空，实际实现时可以通过领域事件机制处理
    }
    
    // Getters
    public InventoryId getId() { return id; }
    public SkuId getSkuId() { return skuId; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public Quantity getTotalQuantity() { return totalQuantity; }
    public Quantity getAvailableQuantity() { return availableQuantity; }
    public Quantity getReservedQuantity() { return reservedQuantity; }
    public Quantity getFrozenQuantity() { return frozenQuantity; }
    public Quantity getSafetyStockQuantity() { return safetyStockQuantity; }
    public InventoryStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(id, inventory.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", skuId=" + skuId +
                ", warehouseId=" + warehouseId +
                ", totalQuantity=" + totalQuantity +
                ", availableQuantity=" + availableQuantity +
                ", status=" + status +
                '}';
    }
}