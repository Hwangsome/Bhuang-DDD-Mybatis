package com.ecommerce.product.infrastructure.mapper;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

/**
 * 商品数据访问映射器 - MyBatis-Plus
 * 职责：商品数据的CRUD操作和复杂查询
 */
@Mapper
public interface ProductDataMapper extends BaseMapper<ProductDataMapper.ProductDataObject> {
    
    /**
     * 根据分类路径前缀查询商品
     */
    @Select("SELECT * FROM product WHERE category_id LIKE CONCAT(#{categoryPath}, '%') AND status != 'DELETED'")
    List<ProductDataObject> findByCategoryPrefix(@Param("categoryPath") String categoryPath);
    
    /**
     * 根据关键词搜索商品
     */
    @Select("SELECT * FROM product WHERE (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) AND status != 'DELETED'")
    List<ProductDataObject> findByKeyword(@Param("keyword") String keyword);
    
    /**
     * 复合条件查询
     */
    @Select("""
        <script>
        SELECT * FROM product 
        WHERE status != 'DELETED'
        <if test="keyword != null and keyword != ''">
            AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        <if test="categoryId != null and categoryId != ''">
            AND category_id = #{categoryId}
        </if>
        <if test="brand != null and brand != ''">
            AND brand = #{brand}
        </if>
        <if test="status != null and status != ''">
            AND status = #{status}
        </if>
        ORDER BY updated_at DESC
        LIMIT #{offset}, #{limit}
        </script>
    """)
    List<ProductDataObject> findByCriteria(@Param("keyword") String keyword,
                                         @Param("categoryId") String categoryId,
                                         @Param("brand") String brand,
                                         @Param("status") String status,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
    
    /**
     * 复合条件统计数量
     */
    @Select("""
        <script>
        SELECT COUNT(*) FROM product 
        WHERE status != 'DELETED'
        <if test="keyword != null and keyword != ''">
            AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        <if test="categoryId != null and categoryId != ''">
            AND category_id = #{categoryId}
        </if>
        <if test="brand != null and brand != ''">
            AND brand = #{brand}
        </if>
        <if test="status != null and status != ''">
            AND status = #{status}
        </if>
        </script>
    """)
    long countByCriteria(@Param("keyword") String keyword,
                        @Param("categoryId") String categoryId,
                        @Param("brand") String brand,
                        @Param("status") String status);
    
    /**
     * 查询最新商品
     */
    @Select("SELECT * FROM product WHERE status = 'ACTIVE' ORDER BY created_at DESC LIMIT #{limit}")
    List<ProductDataObject> findLatestProducts(@Param("limit") int limit);
    
    /**
     * 检查名称是否重复
     */
    @Select("SELECT COUNT(*) FROM product WHERE name = #{name} AND id != #{excludeId} AND status != 'DELETED'")
    long countByNameAndIdNot(@Param("name") String name, @Param("excludeId") String excludeId);
    
    /**
     * 商品数据对象
     */
    @TableName("product")
    class ProductDataObject {
        
        @TableId(type = IdType.INPUT)
        private String id;
        
        @TableField("name")
        private String name;
        
        @TableField("description")
        private String description;
        
        @TableField("category_id")
        private String categoryId;
        
        @TableField("brand")
        private String brand;
        
        @TableField("status")
        private String status;
        
        @TableField(value = "created_at", fill = FieldFill.INSERT)
        private LocalDateTime createdAt;
        
        @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
        private LocalDateTime updatedAt;
        
        @TableField(exist = false)
        private List<SkuDataObject> skus;
        
        // Constructors
        public ProductDataObject() {}
        
        public ProductDataObject(String id, String name, String description, String categoryId, 
                               String brand, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.categoryId = categoryId;
            this.brand = brand;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCategoryId() { return categoryId; }
        public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
        
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        
        public List<SkuDataObject> getSkus() { return skus; }
        public void setSkus(List<SkuDataObject> skus) { this.skus = skus; }
    }
    
    /**
     * SKU数据对象
     */
    @TableName("sku")
    class SkuDataObject {
        
        @TableId(type = IdType.INPUT)
        private String id;
        
        @TableField("product_id")
        private String productId;
        
        @TableField("name")
        private String name;
        
        @TableField("specification_json")
        private String specificationJson;
        
        @TableField("price_amount")
        private BigDecimal priceAmount;
        
        @TableField("price_currency")
        private String priceCurrency;
        
        @TableField("barcode")
        private String barcode;
        
        @TableField("status")
        private String status;
        
        @TableField(value = "created_at", fill = FieldFill.INSERT)
        private LocalDateTime createdAt;
        
        @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
        private LocalDateTime updatedAt;
        
        // Constructors
        public SkuDataObject() {}
        
        public SkuDataObject(String id, String productId, String name, String specificationJson,
                           BigDecimal priceAmount, String priceCurrency, String barcode, String status,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.productId = productId;
            this.name = name;
            this.specificationJson = specificationJson;
            this.priceAmount = priceAmount;
            this.priceCurrency = priceCurrency;
            this.barcode = barcode;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getSpecificationJson() { return specificationJson; }
        public void setSpecificationJson(String specificationJson) { this.specificationJson = specificationJson; }
        
        public BigDecimal getPriceAmount() { return priceAmount; }
        public void setPriceAmount(BigDecimal priceAmount) { this.priceAmount = priceAmount; }
        
        public String getPriceCurrency() { return priceCurrency; }
        public void setPriceCurrency(String priceCurrency) { this.priceCurrency = priceCurrency; }
        
        public String getBarcode() { return barcode; }
        public void setBarcode(String barcode) { this.barcode = barcode; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}