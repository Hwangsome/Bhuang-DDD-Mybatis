package com.ecommerce.inventory.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.inventory.infrastructure.entity.InventoryPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface InventoryPlusMapper extends BaseMapper<InventoryPO> {
    
    @Select("SELECT * FROM inventory WHERE sku_id = #{skuId}")
    List<InventoryPO> findBySkuId(@Param("skuId") String skuId);
    
    @Select("SELECT * FROM inventory WHERE warehouse_id = #{warehouseId}")
    List<InventoryPO> findByWarehouseId(@Param("warehouseId") String warehouseId);
    
    @Select("SELECT * FROM inventory WHERE sku_id = #{skuId} AND warehouse_id = #{warehouseId}")
    InventoryPO findBySkuIdAndWarehouseId(@Param("skuId") String skuId, @Param("warehouseId") String warehouseId);
    
    @Select("SELECT * FROM inventory WHERE status = #{status}")
    List<InventoryPO> findByStatus(@Param("status") String status);
    
    @Select("SELECT * FROM inventory WHERE available_quantity < #{threshold}")
    List<InventoryPO> findLowStockInventories(@Param("threshold") Integer threshold);
}