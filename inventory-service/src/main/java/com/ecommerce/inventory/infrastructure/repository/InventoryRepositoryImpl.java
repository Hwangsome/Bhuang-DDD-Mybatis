package com.ecommerce.inventory.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.inventory.domain.entity.Inventory;
import com.ecommerce.inventory.domain.entity.InventoryStatus;
import com.ecommerce.inventory.domain.repository.InventoryRepository;
import com.ecommerce.inventory.domain.valueobject.InventoryId;
import com.ecommerce.inventory.domain.valueobject.SkuId;
import com.ecommerce.inventory.domain.valueobject.WarehouseId;
import com.ecommerce.inventory.infrastructure.entity.InventoryPO;
import com.ecommerce.inventory.infrastructure.mapper.InventoryDataMapper;
import com.ecommerce.inventory.infrastructure.mapper.InventoryPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InventoryRepositoryImpl implements InventoryRepository {
    
    @Autowired
    private InventoryPlusMapper inventoryPlusMapper;
    
    @Autowired
    private InventoryDataMapper inventoryDataMapper;

    @Override
    public Inventory save(Inventory inventory) {
        InventoryPO inventoryPO = inventoryDataMapper.inventoryToInventoryPO(inventory);
        if (inventoryPO.getId() == null) {
            inventoryPlusMapper.insert(inventoryPO);
        } else {
            inventoryPlusMapper.updateById(inventoryPO);
        }
        return inventoryDataMapper.inventoryPOToInventory(inventoryPO);
    }

    @Override
    public Optional<Inventory> findById(InventoryId inventoryId) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getInventoryId, inventoryId.getValue());
        InventoryPO inventoryPO = inventoryPlusMapper.selectOne(queryWrapper);
        return inventoryPO != null ? 
            Optional.of(inventoryDataMapper.inventoryPOToInventory(inventoryPO)) : 
            Optional.empty();
    }

    @Override
    public List<Inventory> findBySkuId(SkuId skuId) {
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.findBySkuId(skuId.getValue());
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    public Optional<Inventory> findBySkuIdAndWarehouseId(SkuId skuId, WarehouseId warehouseId) {
        InventoryPO inventoryPO = inventoryPlusMapper.findBySkuIdAndWarehouseId(
            skuId.getValue(), warehouseId.getValue());
        return inventoryPO != null ? 
            Optional.of(inventoryDataMapper.inventoryPOToInventory(inventoryPO)) : 
            Optional.empty();
    }

    public void delete(InventoryId inventoryId) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getInventoryId, inventoryId.getValue());
        inventoryPlusMapper.delete(queryWrapper);
    }

    @Override
    public boolean existsById(InventoryId inventoryId) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getInventoryId, inventoryId.getValue());
        return inventoryPlusMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Optional<Inventory> findBySkuAndWarehouse(SkuId skuId, WarehouseId warehouseId) {
        return findBySkuIdAndWarehouseId(skuId, warehouseId);
    }

    @Override
    public boolean existsBySkuAndWarehouse(SkuId skuId, WarehouseId warehouseId) {
        InventoryPO inventoryPO = inventoryPlusMapper.findBySkuIdAndWarehouseId(
            skuId.getValue(), warehouseId.getValue());
        return inventoryPO != null;
    }

    @Override
    public List<Inventory> findByWarehouseId(WarehouseId warehouseId) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getWarehouseId, warehouseId.getValue());
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.selectList(queryWrapper);
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findByStatus(InventoryStatus status) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getStatus, status.name());
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.selectList(queryWrapper);
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findBySkuIdAndStatus(SkuId skuId, InventoryStatus status) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getSkuId, skuId.getValue())
                   .eq(InventoryPO::getStatus, status.name());
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.selectList(queryWrapper);
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findByWarehouseIdAndStatus(WarehouseId warehouseId, InventoryStatus status) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getWarehouseId, warehouseId.getValue())
                   .eq(InventoryPO::getStatus, status.name());
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.selectList(queryWrapper);
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findBelowSafetyStock() {
        // Basic implementation - returns empty list
        return List.of();
    }

    @Override
    public List<Inventory> findOutOfStock() {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getAvailableQuantity, 0);
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.selectList(queryWrapper);
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findWithReservedStock() {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.gt(InventoryPO::getReservedQuantity, 0);
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.selectList(queryWrapper);
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findWithFrozenStock() {
        // Basic implementation - returns empty list (no frozen stock field in current schema)
        return List.of();
    }

    @Override
    public List<Inventory> findAll(int page, int size) {
        // Basic implementation - returns all records
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.selectList(null);
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findByCriteria(SkuId skuId, WarehouseId warehouseId, InventoryStatus status, int page, int size) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        if (skuId != null) queryWrapper.eq(InventoryPO::getSkuId, skuId.getValue());
        if (warehouseId != null) queryWrapper.eq(InventoryPO::getWarehouseId, warehouseId.getValue());
        if (status != null) queryWrapper.eq(InventoryPO::getStatus, status.name());
        
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.selectList(queryWrapper);
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return inventoryPlusMapper.selectCount(null);
    }

    @Override
    public long countByCriteria(SkuId skuId, WarehouseId warehouseId, InventoryStatus status) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        if (skuId != null) queryWrapper.eq(InventoryPO::getSkuId, skuId.getValue());
        if (warehouseId != null) queryWrapper.eq(InventoryPO::getWarehouseId, warehouseId.getValue());
        if (status != null) queryWrapper.eq(InventoryPO::getStatus, status.name());
        
        return inventoryPlusMapper.selectCount(queryWrapper);
    }

    @Override
    public long countBySkuId(SkuId skuId) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getSkuId, skuId.getValue());
        return inventoryPlusMapper.selectCount(queryWrapper);
    }

    @Override
    public long countByWarehouseId(WarehouseId warehouseId) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getWarehouseId, warehouseId.getValue());
        return inventoryPlusMapper.selectCount(queryWrapper);
    }

    @Override
    public long countByStatus(InventoryStatus status) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getStatus, status.name());
        return inventoryPlusMapper.selectCount(queryWrapper);
    }

    @Override
    public void deleteById(InventoryId inventoryId) {
        delete(inventoryId);
    }

    @Override
    public void deleteBySkuId(SkuId skuId) {
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InventoryPO::getSkuId, skuId.getValue());
        inventoryPlusMapper.delete(queryWrapper);
    }

    @Override
    public List<Inventory> saveAll(List<Inventory> inventories) {
        return inventories.stream()
            .map(this::save)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findByIds(List<InventoryId> inventoryIds) {
        List<String> ids = inventoryIds.stream()
            .map(InventoryId::getValue)
            .collect(Collectors.toList());
        
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(InventoryPO::getInventoryId, ids);
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.selectList(queryWrapper);
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findBySkuIds(List<SkuId> skuIds) {
        List<String> ids = skuIds.stream()
            .map(SkuId::getValue)
            .collect(Collectors.toList());
        
        LambdaQueryWrapper<InventoryPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(InventoryPO::getSkuId, ids);
        List<InventoryPO> inventoryPOs = inventoryPlusMapper.selectList(queryWrapper);
        return inventoryPOs.stream()
            .map(inventoryDataMapper::inventoryPOToInventory)
            .collect(Collectors.toList());
    }

    @Override
    public List<Inventory> findNeedingReplenishment(WarehouseId warehouseId) {
        // Basic implementation - returns empty list
        return List.of();
    }

    @Override
    public List<Inventory> findActiveInventories() {
        return findByStatus(InventoryStatus.ACTIVE);
    }

    @Override
    public List<Inventory> findActiveInventoriesBySkuId(SkuId skuId) {
        return findBySkuIdAndStatus(skuId, InventoryStatus.ACTIVE);
    }

    @Override
    public List<Inventory> findActiveInventoriesByWarehouseId(WarehouseId warehouseId) {
        return findByWarehouseIdAndStatus(warehouseId, InventoryStatus.ACTIVE);
    }

    @Override
    public boolean updateWithOptimisticLock(Inventory inventory) {
        // Basic implementation using save
        save(inventory);
        return true;
    }

    @Override
    public List<InventoryRepository.InventorySummary> getInventorySummaryBySkuIds(List<SkuId> skuIds) {
        // Basic implementation - returns empty list
        return List.of();
    }

    @Override
    public List<InventoryRepository.InventorySummary> getInventorySummaryByWarehouseId(WarehouseId warehouseId) {
        // Basic implementation - returns empty list
        return List.of();
    }
}