package com.ecommerce.product.domain.valueobject;

import java.util.Objects;

/**
 * 分类标识符 - 值对象
 * 领域概念：商品分类的唯一标识符
 * 特性：不可变、支持层级结构、封装验证逻辑
 */
public final class CategoryId {
    
    private final String value;
    
    private CategoryId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("分类ID不能为空");
        }
        
        if (value.length() > 100) {
            throw new IllegalArgumentException("分类ID长度不能超过100个字符");
        }
        
        // 分类ID格式验证：支持层级路径 /category/subcategory
        if (!value.matches("^(/[a-zA-Z0-9_-]+)+$")) {
            throw new IllegalArgumentException("分类ID必须为路径格式，如：/electronics/phones");
        }
        
        this.value = value;
    }
    
    /**
     * 创建分类ID
     */
    public static CategoryId of(String value) {
        return new CategoryId(value);
    }
    
    /**
     * 创建根分类ID
     */
    public static CategoryId root(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("根分类名称不能为空");
        }
        
        String cleanName = name.toLowerCase().replaceAll("[^a-zA-Z0-9_-]", "");
        return new CategoryId("/" + cleanName);
    }
    
    /**
     * 创建子分类ID
     */
    public CategoryId createChild(String childName) {
        if (childName == null || childName.trim().isEmpty()) {
            throw new IllegalArgumentException("子分类名称不能为空");
        }
        
        String cleanName = childName.toLowerCase().replaceAll("[^a-zA-Z0-9_-]", "");
        return new CategoryId(this.value + "/" + cleanName);
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 获取分类层级
     */
    public int getLevel() {
        return value.split("/").length - 1;
    }
    
    /**
     * 获取分类名称
     */
    public String getName() {
        int lastSlash = value.lastIndexOf("/");
        return lastSlash >= 0 ? value.substring(lastSlash + 1) : value;
    }
    
    /**
     * 获取父分类ID
     */
    public CategoryId getParent() {
        if (getLevel() <= 1) {
            return null;  // 根分类没有父分类
        }
        
        int lastSlash = value.lastIndexOf("/");
        return new CategoryId(value.substring(0, lastSlash));
    }
    
    /**
     * 检查是否为根分类
     */
    public boolean isRoot() {
        return getLevel() == 1;
    }
    
    /**
     * 检查是否为指定分类的子分类
     */
    public boolean isChildOf(CategoryId parent) {
        if (parent == null) {
            return false;
        }
        
        return value.startsWith(parent.value + "/") && getLevel() == parent.getLevel() + 1;
    }
    
    /**
     * 检查是否为指定分类的后代分类
     */
    public boolean isDescendantOf(CategoryId ancestor) {
        if (ancestor == null) {
            return false;
        }
        
        return value.startsWith(ancestor.value + "/") && getLevel() > ancestor.getLevel();
    }
    
    /**
     * 获取路径组件
     */
    public String[] getPathComponents() {
        return value.split("/");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryId that = (CategoryId) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}