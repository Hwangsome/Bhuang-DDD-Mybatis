package com.ecommerce.inventory.infrastructure.mapper;

import com.ecommerce.inventory.domain.entity.Inventory;
import com.ecommerce.inventory.domain.entity.InventoryStatus;
import com.ecommerce.inventory.domain.valueobject.InventoryId;
import com.ecommerce.inventory.domain.valueobject.Quantity;
import com.ecommerce.inventory.domain.valueobject.SkuId;
import com.ecommerce.inventory.domain.valueobject.WarehouseId;
import com.ecommerce.inventory.infrastructure.entity.InventoryPO;
import org.springframework.stereotype.Component;

@Component
public class InventoryDataMapperImpl implements InventoryDataMapper {

    @Override
    public InventoryPO inventoryToInventoryPO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        
        InventoryPO inventoryPO = new InventoryPO();
        inventoryPO.setInventoryId(inventory.getId() != null ? inventory.getId().getValue() : null);
        inventoryPO.setSkuId(inventory.getSkuId() != null ? inventory.getSkuId().getValue() : null);
        inventoryPO.setWarehouseId(inventory.getWarehouseId() != null ? inventory.getWarehouseId().getValue() : null);
        inventoryPO.setAvailableQuantity(inventory.getAvailableQuantity() != null ? inventory.getAvailableQuantity().getValue() : null);
        inventoryPO.setReservedQuantity(inventory.getReservedQuantity() != null ? inventory.getReservedQuantity().getValue() : null);
        inventoryPO.setTotalQuantity(inventory.getTotalQuantity() != null ? inventory.getTotalQuantity().getValue() : null);
        inventoryPO.setStatus(inventory.getStatus() != null ? inventory.getStatus().name() : null);
        
        return inventoryPO;
    }

    @Override
    public Inventory inventoryPOToInventory(InventoryPO inventoryPO) {
        if (inventoryPO == null) {
            return null;
        }
        
        // Use create method and reflection to set additional fields
        Inventory inventory = Inventory.create(
            SkuId.of(inventoryPO.getSkuId()),
            WarehouseId.of(inventoryPO.getWarehouseId()),
            Quantity.of(inventoryPO.getAvailableQuantity()),
            Quantity.zero() // safety stock quantity
        );
        
        // Set additional fields using reflection
        try {
            java.lang.reflect.Field idField = Inventory.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(inventory, InventoryId.of(inventoryPO.getInventoryId()));
            
            java.lang.reflect.Field reservedField = Inventory.class.getDeclaredField("reservedQuantity");
            reservedField.setAccessible(true);
            reservedField.set(inventory, Quantity.of(inventoryPO.getReservedQuantity()));
            
            java.lang.reflect.Field totalField = Inventory.class.getDeclaredField("totalQuantity");
            totalField.setAccessible(true);
            totalField.set(inventory, Quantity.of(inventoryPO.getTotalQuantity()));
            
            java.lang.reflect.Field statusField = Inventory.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(inventory, InventoryStatus.valueOf(inventoryPO.getStatus()));
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to map InventoryPO to Inventory", e);
        }
        
        return inventory;
    }
}