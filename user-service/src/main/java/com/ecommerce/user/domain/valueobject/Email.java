package com.ecommerce.user.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 邮箱值对象 - 封装邮箱格式验证逻辑
 * DDD值对象特征：不可变、自验证、具有业务语义
 */
public final class Email {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private final String value;

    private Email(String value) {
        this.value = Objects.requireNonNull(value, "邮箱不能为空");
        validateFormat(value);
    }

    /**
     * 创建邮箱值对象
     */
    public static Email of(String email) {
        return new Email(email.trim().toLowerCase());
    }

    /**
     * 验证邮箱格式
     */
    private void validateFormat(String email) {
        if (email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("邮箱格式不正确: " + email);
        }
        
        if (email.length() > 254) {
            throw new IllegalArgumentException("邮箱长度不能超过254个字符");
        }
    }

    /**
     * 获取邮箱域名
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    /**
     * 获取邮箱用户名部分
     */
    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }

    /**
     * 判断是否为企业邮箱
     */
    public boolean isCorporateEmail() {
        String domain = getDomain().toLowerCase();
        return !domain.equals("gmail.com") && 
               !domain.equals("yahoo.com") && 
               !domain.equals("hotmail.com") && 
               !domain.equals("qq.com") && 
               !domain.equals("163.com") && 
               !domain.equals("126.com");
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Email{" +
                "value='" + value + '\'' +
                '}';
    }
}