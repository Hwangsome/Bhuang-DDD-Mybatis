package com.ecommerce.product.domain.repository;

import com.ecommerce.product.domain.entity.Sku;
import com.ecommerce.product.domain.entity.SkuStatus;
import com.ecommerce.product.domain.valueobject.ProductId;
import com.ecommerce.product.domain.valueobject.SkuId;

import java.util.List;
import java.util.Optional;

/**
 * SKU仓储接口 - 领域层
 * 职责：定义SKU数据访问的领域接口，与基础设施层解耦
 * 特性：面向领域对象、业务导向的查询方法
 */
public interface SkuRepository {
    
    /**
     * 保存SKU（新增或更新）
     */
    Sku save(Sku sku);
    
    /**
     * 根据ID查找SKU
     */
    Optional<Sku> findById(SkuId skuId);
    
    /**
     * 检查SKU是否存在
     */
    boolean existsById(SkuId skuId);
    
    /**
     * 根据商品ID查找所有SKU
     */
    List<Sku> findByProductId(ProductId productId);
    
    /**
     * 根据商品ID和状态查找SKU
     */
    List<Sku> findByProductIdAndStatus(ProductId productId, SkuStatus status);
    
    /**
     * 根据条形码查找SKU
     */
    Optional<Sku> findByBarcode(String barcode);
    
    /**
     * 检查条形码是否存在
     */
    boolean existsByBarcode(String barcode);
    
    /**
     * 检查条形码是否被其他SKU使用
     */
    boolean existsByBarcodeAndIdNot(String barcode, SkuId excludeId);
    
    /**
     * 根据状态查找SKU
     */
    List<Sku> findByStatus(SkuStatus status);
    
    /**
     * 根据名称模糊查找SKU
     */
    List<Sku> findByNameContaining(String keyword);
    
    /**
     * 分页查询SKU
     */
    List<Sku> findAll(int page, int size);
    
    /**
     * 根据商品ID分页查询SKU
     */
    List<Sku> findByProductId(ProductId productId, int page, int size);
    
    /**
     * 统计SKU数量
     */
    long count();
    
    /**
     * 根据商品ID统计SKU数量
     */
    long countByProductId(ProductId productId);
    
    /**
     * 根据状态统计SKU数量
     */
    long countByStatus(SkuStatus status);
    
    /**
     * 根据商品ID和状态统计SKU数量
     */
    long countByProductIdAndStatus(ProductId productId, SkuStatus status);
    
    /**
     * 删除SKU（物理删除，慎用）
     */
    void deleteById(SkuId skuId);
    
    /**
     * 根据商品ID删除所有SKU
     */
    void deleteByProductId(ProductId productId);
    
    /**
     * 批量保存SKU
     */
    List<Sku> saveAll(List<Sku> skus);
    
    /**
     * 批量查询SKU
     */
    List<Sku> findByIds(List<SkuId> skuIds);
    
    /**
     * 批量查询SKU (别名方法，便于使用)
     */
    default List<Sku> findByIdIn(List<SkuId> skuIds) {
        return findByIds(skuIds);
    }
    
    /**
     * 查找活跃的SKU
     */
    List<Sku> findActiveSkus();
    
    /**
     * 根据商品ID查找活跃的SKU
     */
    List<Sku> findActiveSkusByProductId(ProductId productId);
    
    /**
     * 查找需要更新的SKU（用于定时任务）
     */
    List<Sku> findSkusNeedingUpdate();
    
    /**
     * 批量更新SKU状态
     */
    int updateStatusByProductId(ProductId productId, SkuStatus newStatus);
    
    /**
     * 获取商品的默认SKU
     */
    Optional<Sku> findDefaultSkuByProductId(ProductId productId);
    
    /**
     * 检查商品是否有活跃的SKU
     */
    boolean hasActiveSkus(ProductId productId);
}