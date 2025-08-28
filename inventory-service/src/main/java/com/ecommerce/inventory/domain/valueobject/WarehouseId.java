package com.ecommerce.inventory.domain.valueobject;

import java.util.Objects;

/**
 * 仓库标识符 - 值对象
 * 领域概念：仓库的唯一标识符
 * 特性：不可变、具有业务含义、支持仓库层级管理
 */
public final class WarehouseId {
    
    private final String value;
    
    private WarehouseId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("仓库ID不能为空");
        }
        
        if (value.length() > 50) {
            throw new IllegalArgumentException("仓库ID长度不能超过50个字符");
        }
        
        // 仓库ID格式验证：允许字母、数字、短横线和下划线
        if (!value.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("仓库ID只能包含字母、数字、短横线和下划线");
        }
        
        this.value = value.toUpperCase(); // 统一使用大写
    }
    
    /**
     * 创建仓库ID
     */
    public static WarehouseId of(String value) {
        return new WarehouseId(value);
    }
    
    /**
     * 创建默认仓库ID
     */
    public static WarehouseId defaultWarehouse() {
        return new WarehouseId("DEFAULT");
    }
    
    /**
     * 创建主仓库ID
     */
    public static WarehouseId mainWarehouse() {
        return new WarehouseId("MAIN");
    }
    
    /**
     * 根据地区代码创建仓库ID
     */
    public static WarehouseId regional(String regionCode) {
        if (regionCode == null || regionCode.trim().isEmpty()) {
            throw new IllegalArgumentException("地区代码不能为空");
        }
        
        String cleanCode = regionCode.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        return new WarehouseId("WH-" + cleanCode);
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 检查是否为默认仓库
     */
    public boolean isDefault() {
        return "DEFAULT".equals(value);
    }
    
    /**
     * 检查是否为主仓库
     */
    public boolean isMain() {
        return "MAIN".equals(value);
    }
    
    /**
     * 检查是否为地区仓库
     */
    public boolean isRegional() {
        return value.startsWith("WH-");
    }
    
    /**
     * 获取地区代码（如果是地区仓库）
     */
    public String getRegionCode() {
        if (isRegional() && value.length() > 3) {
            return value.substring(3);
        }
        return null;
    }
    
    /**
     * 获取仓库类型
     */
    public WarehouseType getType() {
        if (isDefault()) {
            return WarehouseType.DEFAULT;
        } else if (isMain()) {
            return WarehouseType.MAIN;
        } else if (isRegional()) {
            return WarehouseType.REGIONAL;
        } else {
            return WarehouseType.CUSTOM;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseId that = (WarehouseId) o;
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
    
    /**
     * 仓库类型枚举
     */
    public enum WarehouseType {
        DEFAULT("默认仓库"),
        MAIN("主仓库"),
        REGIONAL("地区仓库"),
        CUSTOM("自定义仓库");
        
        private final String description;
        
        WarehouseType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}