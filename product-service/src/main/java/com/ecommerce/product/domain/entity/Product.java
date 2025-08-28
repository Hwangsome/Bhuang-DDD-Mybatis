package com.ecommerce.product.domain.entity;

import com.ecommerce.product.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 商品 - 聚合根
 * 领域概念：电商系统中的商品实体，管理商品的基本信息、分类、状态等
 * 职责：商品生命周期管理、业务规则封装、状态一致性保证
 */
public class Product {
    
    private ProductId id;
    private String name;
    private String description;
    private CategoryId categoryId;
    private String brand;
    private List<Sku> skus;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 私有构造函数，强制使用工厂方法
    private Product() {
        this.skus = new ArrayList<>();
        this.status = ProductStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 创建新商品
     */
    public static Product create(String name, String description, CategoryId categoryId, String brand) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        
        if (name.length() > 200) {
            throw new IllegalArgumentException("商品名称长度不能超过200个字符");
        }
        
        if (description != null && description.length() > 2000) {
            throw new IllegalArgumentException("商品描述长度不能超过2000个字符");
        }
        
        if (categoryId == null) {
            throw new IllegalArgumentException("商品分类不能为空");
        }
        
        if (brand != null && brand.length() > 100) {
            throw new IllegalArgumentException("品牌名称长度不能超过100个字符");
        }
        
        Product product = new Product();
        product.id = ProductId.generate();
        product.name = name.trim();
        product.description = description != null ? description.trim() : null;
        product.categoryId = categoryId;
        product.brand = brand != null ? brand.trim() : null;
        
        return product;
    }
    
    /**
     * 从持久化数据重建商品
     */
    public static Product restore(ProductId id, String name, String description, CategoryId categoryId, 
                                String brand, List<Sku> skus, ProductStatus status, 
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        Product product = new Product();
        product.id = id;
        product.name = name;
        product.description = description;
        product.categoryId = categoryId;
        product.brand = brand;
        product.skus = skus != null ? new ArrayList<>(skus) : new ArrayList<>();
        product.status = status != null ? status : ProductStatus.DRAFT;
        product.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        product.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        
        return product;
    }
    
    /**
     * 更新商品基本信息
     */
    public void updateBasicInfo(String name, String description, String brand) {
        if (status == ProductStatus.DELETED) {
            throw new IllegalStateException("已删除的商品不能修改");
        }
        
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        
        if (name.length() > 200) {
            throw new IllegalArgumentException("商品名称长度不能超过200个字符");
        }
        
        if (description != null && description.length() > 2000) {
            throw new IllegalArgumentException("商品描述长度不能超过2000个字符");
        }
        
        if (brand != null && brand.length() > 100) {
            throw new IllegalArgumentException("品牌名称长度不能超过100个字符");
        }
        
        this.name = name.trim();
        this.description = description != null ? description.trim() : null;
        this.brand = brand != null ? brand.trim() : null;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更改分类
     */
    public void changeCategory(CategoryId newCategoryId) {
        if (status == ProductStatus.DELETED) {
            throw new IllegalStateException("已删除的商品不能修改分类");
        }
        
        if (newCategoryId == null) {
            throw new IllegalArgumentException("商品分类不能为空");
        }
        
        if (Objects.equals(this.categoryId, newCategoryId)) {
            return;
        }
        
        this.categoryId = newCategoryId;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 添加SKU
     */
    public void addSku(String name, ProductSpecification specification, Money price, String barcode) {
        if (status == ProductStatus.DELETED) {
            throw new IllegalStateException("已删除的商品不能添加SKU");
        }
        
        // 检查规格是否重复
        if (specification != null && !specification.isEmpty()) {
            boolean exists = skus.stream()
                    .anyMatch(sku -> sku.getSpecification().equals(specification));
            if (exists) {
                throw new IllegalArgumentException("相同规格的SKU已存在");
            }
        }
        
        // 检查条形码是否重复
        if (barcode != null && !barcode.trim().isEmpty()) {
            boolean barcodeExists = skus.stream()
                    .anyMatch(sku -> Objects.equals(sku.getBarcode(), barcode.trim()));
            if (barcodeExists) {
                throw new IllegalArgumentException("相同条形码的SKU已存在");
            }
        }
        
        Sku sku = Sku.create(this.id, name, specification, price, barcode);
        skus.add(sku);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 移除SKU
     */
    public void removeSku(SkuId skuId) {
        if (status == ProductStatus.DELETED) {
            throw new IllegalStateException("已删除的商品不能移除SKU");
        }
        
        if (skuId == null) {
            throw new IllegalArgumentException("SKU ID不能为空");
        }
        
        boolean removed = skus.removeIf(sku -> sku.getId().equals(skuId));
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 更新SKU价格
     */
    public void updateSkuPrice(SkuId skuId, Money newPrice) {
        if (status == ProductStatus.DELETED) {
            throw new IllegalStateException("已删除的商品不能修改SKU价格");
        }
        
        Sku sku = findSkuById(skuId);
        if (sku == null) {
            throw new IllegalArgumentException("SKU不存在: " + skuId);
        }
        
        sku.updatePrice(newPrice);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 启用SKU
     */
    public void enableSku(SkuId skuId) {
        updateSkuStatus(skuId, SkuStatus.ACTIVE);
    }
    
    /**
     * 禁用SKU
     */
    public void disableSku(SkuId skuId) {
        updateSkuStatus(skuId, SkuStatus.INACTIVE);
    }
    
    /**
     * 更新SKU状态
     */
    private void updateSkuStatus(SkuId skuId, SkuStatus newStatus) {
        if (status == ProductStatus.DELETED) {
            throw new IllegalStateException("已删除的商品不能修改SKU状态");
        }
        
        Sku sku = findSkuById(skuId);
        if (sku == null) {
            throw new IllegalArgumentException("SKU不存在: " + skuId);
        }
        
        sku.updateStatus(newStatus);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 发布商品
     */
    public void publish() {
        if (status == ProductStatus.DELETED) {
            throw new IllegalStateException("已删除的商品不能发布");
        }
        
        if (skus.isEmpty()) {
            throw new IllegalStateException("商品必须至少有一个SKU才能发布");
        }
        
        boolean hasActiveSku = skus.stream().anyMatch(sku -> sku.getStatus() == SkuStatus.ACTIVE);
        if (!hasActiveSku) {
            throw new IllegalStateException("商品必须至少有一个启用的SKU才能发布");
        }
        
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 下架商品
     */
    public void unpublish() {
        if (status == ProductStatus.DELETED) {
            throw new IllegalStateException("已删除的商品不能下架");
        }
        
        this.status = ProductStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 删除商品
     */
    public void delete() {
        this.status = ProductStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 查找SKU
     */
    public Sku findSkuById(SkuId skuId) {
        if (skuId == null) {
            return null;
        }
        
        return skus.stream()
                .filter(sku -> sku.getId().equals(skuId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取活跃的SKU列表
     */
    public List<Sku> getActiveSkus() {
        return skus.stream()
                .filter(sku -> sku.getStatus() == SkuStatus.ACTIVE)
                .toList();
    }
    
    /**
     * 获取价格范围
     */
    public PriceRange getPriceRange() {
        List<Sku> activeSkus = getActiveSkus();
        if (activeSkus.isEmpty()) {
            return null;
        }
        
        Money minPrice = activeSkus.stream()
                .map(Sku::getPrice)
                .min(Money::compareTo)
                .orElse(null);
                
        Money maxPrice = activeSkus.stream()
                .map(Sku::getPrice)
                .max(Money::compareTo)
                .orElse(null);
                
        return new PriceRange(minPrice, maxPrice);
    }
    
    /**
     * 检查是否可以购买
     */
    public boolean canBePurchased() {
        return status == ProductStatus.ACTIVE && !getActiveSkus().isEmpty();
    }
    
    /**
     * 检查是否有指定规格的SKU
     */
    public boolean hasSpecification(ProductSpecification specification) {
        return skus.stream().anyMatch(sku -> sku.getSpecification().equals(specification));
    }
    
    /**
     * 激活商品
     */
    public void activate() {
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 停用商品
     */
    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新分类
     */
    public void updateCategory(CategoryId categoryId) {
        this.categoryId = categoryId;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public ProductId getId() { return id; }
    public ProductId getProductId() { return id; } // 别名方法，便于使用
    public String getName() { return name; }
    public String getDescription() { return description; }
    public CategoryId getCategoryId() { return categoryId; }
    public String getBrand() { return brand; }
    public String getBrandName() { return brand; } // 别名方法，便于使用
    public List<Sku> getSkus() { return Collections.unmodifiableList(skus); }
    public ProductStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", brand='" + brand + '\'' +
                ", status=" + status +
                ", skuCount=" + skus.size() +
                '}';
    }
    
    /**
     * 价格区间内部类
     */
    public static class PriceRange {
        private final Money minPrice;
        private final Money maxPrice;
        
        public PriceRange(Money minPrice, Money maxPrice) {
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
        }
        
        public Money getMinPrice() { return minPrice; }
        public Money getMaxPrice() { return maxPrice; }
        
        public boolean isSinglePrice() {
            return minPrice != null && maxPrice != null && minPrice.equals(maxPrice);
        }
    }
}