package com.ecommerce.inventory.infrastructure.mapper;

import com.ecommerce.inventory.domain.entity.Inventory;
import com.ecommerce.inventory.infrastructure.entity.InventoryPO;

public interface InventoryDataMapper {
    
    InventoryPO inventoryToInventoryPO(Inventory inventory);
    
    Inventory inventoryPOToInventory(InventoryPO inventoryPO);
}