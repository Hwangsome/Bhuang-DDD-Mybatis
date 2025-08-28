package com.ecommerce.inventory.domain.valueobject;

import java.util.Objects;

/**
 * 数量 - 值对象
 * 领域概念：库存数量，支持各种库存操作
 * 特性：不可变、业务规则封装、安全运算
 */
public final class Quantity {
    
    private final int value;
    
    private Quantity(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("数量不能为负数");
        }
        
        if (value > 999999999) {
            throw new IllegalArgumentException("数量不能超过999,999,999");
        }
        
        this.value = value;
    }
    
    /**
     * 创建数量
     */
    public static Quantity of(int value) {
        return new Quantity(value);
    }
    
    /**
     * 零数量
     */
    public static Quantity zero() {
        return new Quantity(0);
    }
    
    /**
     * 一个单位数量
     */
    public static Quantity one() {
        return new Quantity(1);
    }
    
    public int getValue() {
        return value;
    }
    
    /**
     * 检查是否为零
     */
    public boolean isZero() {
        return value == 0;
    }
    
    /**
     * 检查是否为正数
     */
    public boolean isPositive() {
        return value > 0;
    }
    
    /**
     * 加法运算
     */
    public Quantity add(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("数量参数不能为空");
        }
        
        long result = (long) this.value + other.value;
        if (result > 999999999) {
            throw new IllegalArgumentException("数量运算结果溢出");
        }
        
        return new Quantity((int) result);
    }
    
    /**
     * 减法运算
     */
    public Quantity subtract(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("数量参数不能为空");
        }
        
        int result = this.value - other.value;
        if (result < 0) {
            throw new IllegalArgumentException("数量减法运算结果不能为负数");
        }
        
        return new Quantity(result);
    }
    
    /**
     * 乘法运算
     */
    public Quantity multiply(int multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("乘数不能为负数");
        }
        
        long result = (long) this.value * multiplier;
        if (result > 999999999) {
            throw new IllegalArgumentException("数量运算结果溢出");
        }
        
        return new Quantity((int) result);
    }
    
    /**
     * 比较大小
     */
    public int compareTo(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("比较对象不能为空");
        }
        
        return Integer.compare(this.value, other.value);
    }
    
    /**
     * 大于比较
     */
    public boolean greaterThan(Quantity other) {
        return compareTo(other) > 0;
    }
    
    /**
     * 小于比较
     */
    public boolean lessThan(Quantity other) {
        return compareTo(other) < 0;
    }
    
    /**
     * 大于等于比较
     */
    public boolean greaterThanOrEqual(Quantity other) {
        return compareTo(other) >= 0;
    }
    
    /**
     * 小于等于比较
     */
    public boolean lessThanOrEqual(Quantity other) {
        return compareTo(other) <= 0;
    }
    
    /**
     * 检查是否足够（大于等于指定数量）
     */
    public boolean isSufficient(Quantity required) {
        return greaterThanOrEqual(required);
    }
    
    /**
     * 检查是否不足（小于指定数量）
     */
    public boolean isInsufficient(Quantity required) {
        return lessThan(required);
    }
    
    /**
     * 获取最小值
     */
    public static Quantity min(Quantity a, Quantity b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("数量参数不能为空");
        }
        
        return a.lessThanOrEqual(b) ? a : b;
    }
    
    /**
     * 获取最大值
     */
    public static Quantity max(Quantity a, Quantity b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("数量参数不能为空");
        }
        
        return a.greaterThanOrEqual(b) ? a : b;
    }
    
    /**
     * 格式化显示
     */
    public String format() {
        if (value >= 1000000) {
            return String.format("%.1fM", value / 1000000.0);
        } else if (value >= 1000) {
            return String.format("%.1fK", value / 1000.0);
        } else {
            return String.valueOf(value);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantity quantity = (Quantity) o;
        return value == quantity.value;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}