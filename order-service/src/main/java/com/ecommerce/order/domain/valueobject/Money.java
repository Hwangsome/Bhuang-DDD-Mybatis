package com.ecommerce.order.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * 货币金额 - 值对象（订单服务使用）
 * 领域概念：包含金额和货币单位的货币值
 * 特性：不可变、精确计算、货币单位约束
 */
public final class Money {
    
    private final BigDecimal amount;
    private final Currency currency;
    
    private Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("金额不能为空");
        }
        
        if (currency == null) {
            throw new IllegalArgumentException("货币单位不能为空");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额不能为负数");
        }
        
        // 设置货币精度
        int scale = currency.getDefaultFractionDigits();
        this.amount = amount.setScale(scale, RoundingMode.HALF_UP);
        this.currency = currency;
    }
    
    /**
     * 创建货币金额
     */
    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }
    
    /**
     * 创建货币金额（字符串金额）
     */
    public static Money of(String amount, Currency currency) {
        if (amount == null || amount.trim().isEmpty()) {
            throw new IllegalArgumentException("金额字符串不能为空");
        }
        
        try {
            return new Money(new BigDecimal(amount), currency);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的金额格式: " + amount);
        }
    }
    
    /**
     * 创建人民币金额
     */
    public static Money cny(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("CNY"));
    }
    
    /**
     * 创建人民币金额（字符串）
     */
    public static Money cny(String amount) {
        return of(amount, Currency.getInstance("CNY"));
    }
    
    /**
     * 创建美元金额
     */
    public static Money usd(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("USD"));
    }
    
    /**
     * 创建零金额
     */
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    /**
     * 检查是否为零
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * 检查是否为正数
     */
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 加法运算
     */
    public Money add(Money other) {
        checkSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    /**
     * 减法运算
     */
    public Money subtract(Money other) {
        checkSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("减法运算结果不能为负数");
        }
        return new Money(result, this.currency);
    }
    
    /**
     * 乘法运算
     */
    public Money multiply(BigDecimal multiplier) {
        if (multiplier == null) {
            throw new IllegalArgumentException("乘数不能为空");
        }
        
        if (multiplier.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("乘数不能为负数");
        }
        
        return new Money(this.amount.multiply(multiplier), this.currency);
    }
    
    /**
     * 除法运算
     */
    public Money divide(BigDecimal divisor) {
        if (divisor == null) {
            throw new IllegalArgumentException("除数不能为空");
        }
        
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("除数不能为零");
        }
        
        if (divisor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("除数不能为负数");
        }
        
        return new Money(this.amount.divide(divisor, currency.getDefaultFractionDigits(), RoundingMode.HALF_UP), this.currency);
    }
    
    /**
     * 比较大小
     */
    public int compareTo(Money other) {
        checkSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }
    
    /**
     * 大于比较
     */
    public boolean greaterThan(Money other) {
        return compareTo(other) > 0;
    }
    
    /**
     * 小于比较
     */
    public boolean lessThan(Money other) {
        return compareTo(other) < 0;
    }
    
    /**
     * 大于等于比较
     */
    public boolean greaterThanOrEqual(Money other) {
        return compareTo(other) >= 0;
    }
    
    /**
     * 小于等于比较
     */
    public boolean lessThanOrEqual(Money other) {
        return compareTo(other) <= 0;
    }
    
    /**
     * 检查货币单位是否相同
     */
    private void checkSameCurrency(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("比较对象不能为空");
        }
        
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("货币单位不匹配: " + this.currency + " vs " + other.currency);
        }
    }
    
    /**
     * 格式化显示
     */
    public String format() {
        return currency.getSymbol() + " " + amount.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
    
    @Override
    public String toString() {
        return format();
    }
}