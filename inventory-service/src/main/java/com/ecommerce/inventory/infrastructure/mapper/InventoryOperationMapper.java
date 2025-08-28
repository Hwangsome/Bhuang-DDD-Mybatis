package com.ecommerce.inventory.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.inventory.infrastructure.entity.InventoryOperationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface InventoryOperationMapper extends BaseMapper<InventoryOperationPO> {
    
    @Select("SELECT * FROM inventory_operation WHERE inventory_id = #{inventoryId}")
    List<InventoryOperationPO> findByInventoryId(@Param("inventoryId") String inventoryId);
    
    @Select("SELECT * FROM inventory_operation WHERE operation_type = #{operationType}")
    List<InventoryOperationPO> findByOperationType(@Param("operationType") String operationType);
    
    @Select("SELECT * FROM inventory_operation WHERE reference_id = #{referenceId}")
    List<InventoryOperationPO> findByReferenceId(@Param("referenceId") String referenceId);
    
    @Select("SELECT * FROM inventory_operation WHERE reference_type = #{referenceType}")
    List<InventoryOperationPO> findByReferenceType(@Param("referenceType") String referenceType);
}