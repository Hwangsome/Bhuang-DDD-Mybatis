package com.ecommerce.product.domain.entity;

import com.ecommerce.product.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * SKU（库存保持单位）- 实体
 * 领域概念：商品的最小销售单位，包含具体的规格和价格
 * 职责：管理具体规格商品的信息、价格和状态
 */
public class Sku {
    
    private SkuId id;
    private ProductId productId;
    private String name;
    private ProductSpecification specification;
    private Money price;
    private String barcode;
    private SkuStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 私有构造函数
    private Sku() {
        this.status = SkuStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 创建新SKU
     */
    public static Sku create(ProductId productId, String name, ProductSpecification specification, 
                           Money price, String barcode) {
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU名称不能为空");
        }
        
        if (name.length() > 200) {
            throw new IllegalArgumentException("SKU名称长度不能超过200个字符");
        }
        
        if (price == null) {
            throw new IllegalArgumentException("SKU价格不能为空");
        }
        
        if (!price.isPositive()) {
            throw new IllegalArgumentException("SKU价格必须大于0");
        }
        
        if (barcode != null && !barcode.trim().isEmpty()) {
            if (barcode.length() > 50) {
                throw new IllegalArgumentException("条形码长度不能超过50个字符");
            }
            
            // 简单的条形码格式验证（数字和字母）
            if (!barcode.matches("^[a-zA-Z0-9]+$")) {
                throw new IllegalArgumentException("条形码只能包含字母和数字");
            }
        }
        
        Sku sku = new Sku();
        sku.id = SkuId.generateFromProduct(productId, specification != null ? specification.toSpecString() : null);
        sku.productId = productId;
        sku.name = name.trim();
        sku.specification = specification != null ? specification : ProductSpecification.empty();
        sku.price = price;
        sku.barcode = barcode != null && !barcode.trim().isEmpty() ? barcode.trim() : null;
        
        return sku;
    }
    
    /**
     * 从持久化数据重建SKU
     */
    public static Sku restore(SkuId id, ProductId productId, String name, ProductSpecification specification,
                            Money price, String barcode, SkuStatus status,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        Sku sku = new Sku();
        sku.id = id;
        sku.productId = productId;
        sku.name = name;
        sku.specification = specification != null ? specification : ProductSpecification.empty();
        sku.price = price;
        sku.barcode = barcode;
        sku.status = status != null ? status : SkuStatus.ACTIVE;
        sku.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        sku.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        
        return sku;
    }
    
    /**
     * 更新价格
     */
    public void updatePrice(Money newPrice) {
        if (status == SkuStatus.DELETED) {
            throw new IllegalStateException("已删除的SKU不能修改价格");
        }
        
        if (newPrice == null) {
            throw new IllegalArgumentException("SKU价格不能为空");
        }
        
        if (!newPrice.isPositive()) {
            throw new IllegalArgumentException("SKU价格必须大于0");
        }
        
        // 检查货币单位是否一致
        if (!newPrice.getCurrency().equals(this.price.getCurrency())) {
            throw new IllegalArgumentException("不能更改货币单位");
        }
        
        this.price = newPrice;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新名称
     */
    public void updateName(String newName) {
        if (status == SkuStatus.DELETED) {
            throw new IllegalStateException("已删除的SKU不能修改名称");
        }
        
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU名称不能为空");
        }
        
        if (newName.length() > 200) {
            throw new IllegalArgumentException("SKU名称长度不能超过200个字符");
        }
        
        this.name = newName.trim();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新条形码
     */
    public void updateBarcode(String newBarcode) {
        if (status == SkuStatus.DELETED) {
            throw new IllegalStateException("已删除的SKU不能修改条形码");
        }
        
        if (newBarcode != null && !newBarcode.trim().isEmpty()) {
            if (newBarcode.length() > 50) {
                throw new IllegalArgumentException("条形码长度不能超过50个字符");
            }
            
            if (!newBarcode.matches("^[a-zA-Z0-9]+$")) {
                throw new IllegalArgumentException("条形码只能包含字母和数字");
            }
            
            this.barcode = newBarcode.trim();
        } else {
            this.barcode = null;
        }
        
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新状态
     */
    public void updateStatus(SkuStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("SKU状态不能为空");
        }
        
        if (this.status == SkuStatus.DELETED && newStatus != SkuStatus.DELETED) {
            throw new IllegalStateException("已删除的SKU不能恢复");
        }
        
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 启用SKU
     */
    public void activate() {
        updateStatus(SkuStatus.ACTIVE);
    }
    
    /**
     * 禁用SKU
     */
    public void deactivate() {
        updateStatus(SkuStatus.INACTIVE);
    }
    
    /**
     * 删除SKU
     */
    public void delete() {
        updateStatus(SkuStatus.DELETED);
    }
    
    /**
     * 检查是否可以销售
     */
    public boolean canBeSold() {
        return status == SkuStatus.ACTIVE;
    }
    
    /**
     * 检查是否有规格
     */
    public boolean hasSpecification() {
        return specification != null && !specification.isEmpty();
    }
    
    /**
     * 检查是否与指定规格匹配
     */
    public boolean matchesSpecification(ProductSpecification targetSpec) {
        if (targetSpec == null) {
            return specification == null || specification.isEmpty();
        }
        
        return specification != null && specification.equals(targetSpec);
    }
    
    /**
     * 获取显示名称（包含规格信息）
     */
    public String getDisplayName() {
        if (specification == null || specification.isEmpty()) {
            return name;
        }
        
        StringBuilder displayName = new StringBuilder(name);
        displayName.append(" (");
        
        boolean first = true;
        for (Map.Entry<String, String> spec : specification.getSpecifications().entrySet()) {
            if (!first) {
                displayName.append(", ");
            }
            displayName.append(spec.getKey()).append(":").append(spec.getValue());
            first = false;
        }
        
        displayName.append(")");
        return displayName.toString();
    }
    
    /**
     * 计算折扣价格
     */
    public Money calculateDiscountPrice(BigDecimal discountPercentage) {
        return price.discount(discountPercentage);
    }
    
    /**
     * 是否为默认SKU
     */
    public boolean isDefault() {
        return id.isDefault();
    }
    
    // Getters
    public SkuId getId() { return id; }
    public SkuId getSkuId() { return id; } // 别名方法，便于使用
    public ProductId getProductId() { return productId; }
    public String getName() { return name; }
    public ProductSpecification getSpecification() { return specification; }
    public Money getPrice() { return price; }
    public String getBarcode() { return barcode; }
    public SkuStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sku sku = (Sku) o;
        return Objects.equals(id, sku.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Sku{" +
                "id=" + id +
                ", productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}