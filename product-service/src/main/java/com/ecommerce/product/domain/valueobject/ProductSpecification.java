package com.ecommerce.product.domain.valueobject;

import java.util.*;

/**
 * 商品规格 - 值对象
 * 领域概念：商品的规格参数集合
 * 特性：不可变、支持多级规格、验证逻辑
 */
public final class ProductSpecification {
    
    private final Map<String, String> specifications;
    
    private ProductSpecification(Map<String, String> specifications) {
        if (specifications == null) {
            this.specifications = Collections.emptyMap();
        } else {
            // 验证规格参数
            validateSpecifications(specifications);
            this.specifications = Collections.unmodifiableMap(new HashMap<>(specifications));
        }
    }
    
    /**
     * 创建空规格
     */
    public static ProductSpecification empty() {
        return new ProductSpecification(Collections.emptyMap());
    }
    
    /**
     * 创建规格
     */
    public static ProductSpecification of(Map<String, String> specifications) {
        return new ProductSpecification(specifications);
    }
    
    /**
     * 构建器模式创建规格
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 验证规格参数
     */
    private void validateSpecifications(Map<String, String> specs) {
        for (Map.Entry<String, String> entry : specs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (key == null || key.trim().isEmpty()) {
                throw new IllegalArgumentException("规格参数名不能为空");
            }
            
            if (key.length() > 50) {
                throw new IllegalArgumentException("规格参数名长度不能超过50个字符");
            }
            
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("规格参数值不能为空: " + key);
            }
            
            if (value.length() > 200) {
                throw new IllegalArgumentException("规格参数值长度不能超过200个字符: " + key);
            }
        }
    }
    
    public Map<String, String> getSpecifications() {
        return specifications;
    }
    
    /**
     * 检查是否为空
     */
    public boolean isEmpty() {
        return specifications.isEmpty();
    }
    
    /**
     * 获取规格参数值
     */
    public String getValue(String key) {
        return specifications.get(key);
    }
    
    /**
     * 检查是否包含规格参数
     */
    public boolean hasSpecification(String key) {
        return specifications.containsKey(key);
    }
    
    /**
     * 获取所有规格参数名
     */
    public Set<String> getKeys() {
        return specifications.keySet();
    }
    
    /**
     * 获取规格参数数量
     */
    public int size() {
        return specifications.size();
    }
    
    /**
     * 添加规格参数（返回新对象）
     */
    public ProductSpecification addSpecification(String key, String value) {
        Map<String, String> newSpecs = new HashMap<>(specifications);
        newSpecs.put(key, value);
        return new ProductSpecification(newSpecs);
    }
    
    /**
     * 移除规格参数（返回新对象）
     */
    public ProductSpecification removeSpecification(String key) {
        if (!specifications.containsKey(key)) {
            return this;
        }
        
        Map<String, String> newSpecs = new HashMap<>(specifications);
        newSpecs.remove(key);
        return new ProductSpecification(newSpecs);
    }
    
    /**
     * 合并规格参数（返回新对象）
     */
    public ProductSpecification merge(ProductSpecification other) {
        if (other == null || other.isEmpty()) {
            return this;
        }
        
        Map<String, String> newSpecs = new HashMap<>(specifications);
        newSpecs.putAll(other.specifications);
        return new ProductSpecification(newSpecs);
    }
    
    /**
     * 检查是否与另一个规格兼容
     */
    public boolean isCompatibleWith(ProductSpecification other) {
        if (other == null) {
            return false;
        }
        
        for (String key : specifications.keySet()) {
            if (other.hasSpecification(key)) {
                if (!Objects.equals(specifications.get(key), other.getValue(key))) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * 生成规格字符串（用于SKU生成等）
     */
    public String toSpecString() {
        if (specifications.isEmpty()) {
            return "DEFAULT";
        }
        
        return specifications.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .reduce((s1, s2) -> s1 + "," + s2)
                .orElse("DEFAULT");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSpecification that = (ProductSpecification) o;
        return Objects.equals(specifications, that.specifications);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(specifications);
    }
    
    @Override
    public String toString() {
        return "ProductSpecification{" + specifications + "}";
    }
    
    /**
     * 构建器类
     */
    public static class Builder {
        private final Map<String, String> specifications = new HashMap<>();
        
        public Builder addSpecification(String key, String value) {
            if (key != null && !key.trim().isEmpty() && value != null && !value.trim().isEmpty()) {
                specifications.put(key.trim(), value.trim());
            }
            return this;
        }
        
        public Builder color(String color) {
            return addSpecification("颜色", color);
        }
        
        public Builder size(String size) {
            return addSpecification("尺寸", size);
        }
        
        public Builder material(String material) {
            return addSpecification("材质", material);
        }
        
        public Builder brand(String brand) {
            return addSpecification("品牌", brand);
        }
        
        public Builder model(String model) {
            return addSpecification("型号", model);
        }
        
        public Builder capacity(String capacity) {
            return addSpecification("容量", capacity);
        }
        
        public Builder weight(String weight) {
            return addSpecification("重量", weight);
        }
        
        public Builder dimension(String dimension) {
            return addSpecification("尺寸", dimension);
        }
        
        public ProductSpecification build() {
            return new ProductSpecification(specifications);
        }
    }
}