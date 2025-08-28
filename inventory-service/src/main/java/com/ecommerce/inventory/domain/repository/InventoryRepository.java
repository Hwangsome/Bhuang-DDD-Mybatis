package com.ecommerce.inventory.domain.repository;

import com.ecommerce.inventory.domain.entity.Inventory;
import com.ecommerce.inventory.domain.entity.InventoryStatus;
import com.ecommerce.inventory.domain.valueobject.InventoryId;
import com.ecommerce.inventory.domain.valueobject.SkuId;
import com.ecommerce.inventory.domain.valueobject.WarehouseId;

import java.util.List;
import java.util.Optional;

/**
 * 库存仓储接口 - 领域层
 * 职责：定义库存数据访问的领域接口，与基础设施层解耦
 * 特性：面向领域对象、业务导向的查询方法、支持乐观锁
 */
public interface InventoryRepository {
    
    /**
     * 保存库存（新增或更新）
     */
    Inventory save(Inventory inventory);
    
    /**
     * 根据ID查找库存
     */
    Optional<Inventory> findById(InventoryId inventoryId);
    
    /**
     * 检查库存是否存在
     */
    boolean existsById(InventoryId inventoryId);
    
    /**
     * 根据SKU和仓库查找库存
     */
    Optional<Inventory> findBySkuAndWarehouse(SkuId skuId, WarehouseId warehouseId);
    
    /**
     * 检查SKU在指定仓库是否有库存记录
     */
    boolean existsBySkuAndWarehouse(SkuId skuId, WarehouseId warehouseId);
    
    /**
     * 根据SKU查找所有仓库的库存
     */
    List<Inventory> findBySkuId(SkuId skuId);
    
    /**
     * 根据仓库查找所有库存
     */
    List<Inventory> findByWarehouseId(WarehouseId warehouseId);
    
    /**
     * 根据状态查找库存
     */
    List<Inventory> findByStatus(InventoryStatus status);
    
    /**
     * 根据SKU和状态查找库存
     */
    List<Inventory> findBySkuIdAndStatus(SkuId skuId, InventoryStatus status);
    
    /**
     * 根据仓库和状态查找库存
     */
    List<Inventory> findByWarehouseIdAndStatus(WarehouseId warehouseId, InventoryStatus status);
    
    /**
     * 查找低于安全库存的记录
     */
    List<Inventory> findBelowSafetyStock();
    
    /**
     * 查找缺货的库存记录
     */
    List<Inventory> findOutOfStock();
    
    /**
     * 查找有预留库存的记录
     */
    List<Inventory> findWithReservedStock();
    
    /**
     * 查找有冻结库存的记录
     */
    List<Inventory> findWithFrozenStock();
    
    /**
     * 分页查询库存
     */
    List<Inventory> findAll(int page, int size);
    
    /**
     * 根据条件分页查询库存
     */
    List<Inventory> findByCriteria(SkuId skuId, WarehouseId warehouseId, InventoryStatus status, 
                                  int page, int size);
    
    /**
     * 统计库存数量
     */
    long count();
    
    /**
     * 根据条件统计库存数量
     */
    long countByCriteria(SkuId skuId, WarehouseId warehouseId, InventoryStatus status);
    
    /**
     * 根据SKU统计库存数量
     */
    long countBySkuId(SkuId skuId);
    
    /**
     * 根据仓库统计库存数量
     */
    long countByWarehouseId(WarehouseId warehouseId);
    
    /**
     * 根据状态统计库存数量
     */
    long countByStatus(InventoryStatus status);
    
    /**
     * 删除库存（物理删除，慎用）
     */
    void deleteById(InventoryId inventoryId);
    
    /**
     * 根据SKU删除所有库存
     */
    void deleteBySkuId(SkuId skuId);
    
    /**
     * 批量保存库存
     */
    List<Inventory> saveAll(List<Inventory> inventories);
    
    /**
     * 批量查询库存
     */
    List<Inventory> findByIds(List<InventoryId> inventoryIds);
    
    /**
     * 批量查询SKU的库存
     */
    List<Inventory> findBySkuIds(List<SkuId> skuIds);
    
    /**
     * 查找需要补货的库存（低于安全库存）
     */
    List<Inventory> findNeedingReplenishment(WarehouseId warehouseId);
    
    /**
     * 查找活跃的库存记录
     */
    List<Inventory> findActiveInventories();
    
    /**
     * 根据SKU查找活跃的库存记录
     */
    List<Inventory> findActiveInventoriesBySkuId(SkuId skuId);
    
    /**
     * 查找指定仓库的活跃库存记录
     */
    List<Inventory> findActiveInventoriesByWarehouseId(WarehouseId warehouseId);
    
    /**
     * 乐观锁更新库存
     */
    boolean updateWithOptimisticLock(Inventory inventory);
    
    /**
     * 获取库存汇总信息（按SKU）
     */
    List<InventorySummary> getInventorySummaryBySkuIds(List<SkuId> skuIds);
    
    /**
     * 获取库存汇总信息（按仓库）
     */
    List<InventorySummary> getInventorySummaryByWarehouseId(WarehouseId warehouseId);
    
    /**
     * 库存汇总信息内部类
     */
    class InventorySummary {
        private final SkuId skuId;
        private final WarehouseId warehouseId;
        private final int totalQuantity;
        private final int availableQuantity;
        private final int reservedQuantity;
        private final int frozenQuantity;
        
        public InventorySummary(SkuId skuId, WarehouseId warehouseId, 
                              int totalQuantity, int availableQuantity,
                              int reservedQuantity, int frozenQuantity) {
            this.skuId = skuId;
            this.warehouseId = warehouseId;
            this.totalQuantity = totalQuantity;
            this.availableQuantity = availableQuantity;
            this.reservedQuantity = reservedQuantity;
            this.frozenQuantity = frozenQuantity;
        }
        
        // Getters
        public SkuId getSkuId() { return skuId; }
        public WarehouseId getWarehouseId() { return warehouseId; }
        public int getTotalQuantity() { return totalQuantity; }
        public int getAvailableQuantity() { return availableQuantity; }
        public int getReservedQuantity() { return reservedQuantity; }
        public int getFrozenQuantity() { return frozenQuantity; }
    }
}