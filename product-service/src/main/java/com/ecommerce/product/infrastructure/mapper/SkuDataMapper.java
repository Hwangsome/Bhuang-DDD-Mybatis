package com.ecommerce.product.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * SKU数据访问映射器 - MyBatis-Plus
 * 职责：SKU数据的CRUD操作和复杂查询
 */
@Mapper
public interface SkuDataMapper extends BaseMapper<ProductDataMapper.SkuDataObject> {
    
    /**
     * 根据商品ID查询SKU
     */
    @Select("SELECT * FROM sku WHERE product_id = #{productId} AND status != 'DELETED' ORDER BY created_at")
    List<ProductDataMapper.SkuDataObject> findByProductId(@Param("productId") String productId);
    
    /**
     * 根据商品ID和状态查询SKU
     */
    @Select("SELECT * FROM sku WHERE product_id = #{productId} AND status = #{status} ORDER BY created_at")
    List<ProductDataMapper.SkuDataObject> findByProductIdAndStatus(@Param("productId") String productId, 
                                                                  @Param("status") String status);
    
    /**
     * 根据条形码查询SKU
     */
    @Select("SELECT * FROM sku WHERE barcode = #{barcode} AND status != 'DELETED' LIMIT 1")
    ProductDataMapper.SkuDataObject findByBarcode(@Param("barcode") String barcode);
    
    /**
     * 检查条形码是否存在（排除指定ID）
     */
    @Select("SELECT COUNT(*) FROM sku WHERE barcode = #{barcode} AND id != #{excludeId} AND status != 'DELETED'")
    long countByBarcodeAndIdNot(@Param("barcode") String barcode, @Param("excludeId") String excludeId);
    
    /**
     * 根据名称模糊查询SKU
     */
    @Select("SELECT * FROM sku WHERE name LIKE CONCAT('%', #{keyword}, '%') AND status != 'DELETED' ORDER BY created_at DESC")
    List<ProductDataMapper.SkuDataObject> findByNameContaining(@Param("keyword") String keyword);
    
    /**
     * 根据商品ID统计SKU数量
     */
    @Select("SELECT COUNT(*) FROM sku WHERE product_id = #{productId} AND status != 'DELETED'")
    long countByProductId(@Param("productId") String productId);
    
    /**
     * 根据商品ID和状态统计SKU数量
     */
    @Select("SELECT COUNT(*) FROM sku WHERE product_id = #{productId} AND status = #{status}")
    long countByProductIdAndStatus(@Param("productId") String productId, @Param("status") String status);
    
    /**
     * 根据商品ID删除所有SKU
     */
    @Delete("DELETE FROM sku WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") String productId);
    
    /**
     * 查找活跃的SKU
     */
    @Select("SELECT * FROM sku WHERE status = 'ACTIVE' ORDER BY updated_at DESC")
    List<ProductDataMapper.SkuDataObject> findActiveSkus();
    
    /**
     * 根据商品ID查找活跃的SKU
     */
    @Select("SELECT * FROM sku WHERE product_id = #{productId} AND status = 'ACTIVE' ORDER BY created_at")
    List<ProductDataMapper.SkuDataObject> findActiveSkusByProductId(@Param("productId") String productId);
    
    /**
     * 批量更新SKU状态
     */
    @Update("UPDATE sku SET status = #{newStatus}, updated_at = NOW() WHERE product_id = #{productId}")
    int updateStatusByProductId(@Param("productId") String productId, @Param("newStatus") String newStatus);
    
    /**
     * 获取商品的默认SKU
     */
    @Select("SELECT * FROM sku WHERE product_id = #{productId} AND (specification_json IS NULL OR specification_json = '' OR specification_json = '{}') AND status = 'ACTIVE' LIMIT 1")
    ProductDataMapper.SkuDataObject findDefaultSkuByProductId(@Param("productId") String productId);
    
    /**
     * 检查商品是否有活跃的SKU
     */
    @Select("SELECT COUNT(*) > 0 FROM sku WHERE product_id = #{productId} AND status = 'ACTIVE'")
    boolean hasActiveSkus(@Param("productId") String productId);
    
    /**
     * 分页查询SKU
     */
    @Select("SELECT * FROM sku WHERE product_id = #{productId} AND status != 'DELETED' ORDER BY created_at LIMIT #{offset}, #{limit}")
    List<ProductDataMapper.SkuDataObject> findByProductIdWithPagination(@Param("productId") String productId, 
                                                                        @Param("offset") int offset, 
                                                                        @Param("limit") int limit);
    
    /**
     * 根据多个ID批量查询SKU
     */
    @Select("""
        <script>
        SELECT * FROM sku WHERE id IN
        <foreach item="id" collection="ids" open="(" separator="," close=")">
            #{id}
        </foreach>
        AND status != 'DELETED'
        </script>
    """)
    List<ProductDataMapper.SkuDataObject> findByIds(@Param("ids") List<String> ids);
}