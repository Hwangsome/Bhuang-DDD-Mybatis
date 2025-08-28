package com.ecommerce.user.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 手机号值对象 - 封装手机号验证逻辑
 * 支持多国手机号格式，体现DDD值对象的业务规则封装
 */
public final class Phone {
    
    // 中国大陆手机号正则
    private static final Pattern CHINA_MOBILE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    );
    
    // 国际手机号正则 (简化版)
    private static final Pattern INTERNATIONAL_PATTERN = Pattern.compile(
        "^\\+[1-9]\\d{1,14}$"
    );

    private final String value;
    private final String countryCode;

    private Phone(String value, String countryCode) {
        this.value = Objects.requireNonNull(value, "手机号不能为空");
        this.countryCode = Objects.requireNonNull(countryCode, "国家代码不能为空");
        validateFormat(value, countryCode);
    }

    /**
     * 创建中国大陆手机号
     */
    public static Phone ofChina(String phoneNumber) {
        return new Phone(phoneNumber.replaceAll("\\s|-", ""), "+86");
    }

    /**
     * 创建国际手机号
     */
    public static Phone ofInternational(String phoneNumber) {
        String cleanNumber = phoneNumber.replaceAll("\\s|-", "");
        if (!cleanNumber.startsWith("+")) {
            throw new IllegalArgumentException("国际手机号必须以+开头");
        }
        
        // 提取国家代码
        String countryCode = "+" + cleanNumber.substring(1, Math.min(4, cleanNumber.length()));
        return new Phone(cleanNumber, countryCode);
    }

    /**
     * 自动识别手机号类型并创建
     */
    public static Phone of(String phoneNumber) {
        String cleanNumber = phoneNumber.replaceAll("\\s|-", "");
        
        if (cleanNumber.startsWith("+")) {
            return ofInternational(cleanNumber);
        } else if (cleanNumber.startsWith("1") && cleanNumber.length() == 11) {
            return ofChina(cleanNumber);
        } else {
            throw new IllegalArgumentException("无法识别手机号格式: " + phoneNumber);
        }
    }

    /**
     * 验证手机号格式
     */
    private void validateFormat(String phone, String countryCode) {
        if (phone.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        
        if ("+86".equals(countryCode)) {
            // 验证中国大陆手机号
            if (!CHINA_MOBILE_PATTERN.matcher(phone).matches()) {
                throw new IllegalArgumentException("中国大陆手机号格式不正确: " + phone);
            }
        } else {
            // 验证国际手机号
            if (!INTERNATIONAL_PATTERN.matcher(phone).matches()) {
                throw new IllegalArgumentException("国际手机号格式不正确: " + phone);
            }
        }
    }

    /**
     * 获取格式化的手机号 (隐藏中间四位)
     */
    public String getMaskedNumber() {
        if ("+86".equals(countryCode) && value.length() == 11) {
            return value.substring(0, 3) + "****" + value.substring(7);
        } else {
            // 国际号码简单处理
            int length = value.length();
            if (length > 6) {
                return value.substring(0, 3) + "****" + value.substring(length - 3);
            }
            return value.substring(0, 1) + "***" + value.substring(length - 1);
        }
    }

    /**
     * 判断是否为中国大陆手机号
     */
    public boolean isChinaMobile() {
        return "+86".equals(countryCode);
    }

    /**
     * 获取运营商信息 (仅中国大陆)
     */
    public String getCarrier() {
        if (!isChinaMobile()) {
            return "未知";
        }
        
        String prefix = value.substring(0, 3);
        return switch (prefix) {
            case "134", "135", "136", "137", "138", "139", "147", "150", "151", "152", 
                 "157", "158", "159", "172", "178", "182", "183", "184", "187", "188", "198" -> "中国移动";
            case "130", "131", "132", "145", "155", "156", "166", "171", "175", "176", 
                 "185", "186", "196" -> "中国联通";
            case "133", "149", "153", "173", "177", "180", "181", "189", "191", "199" -> "中国电信";
            default -> "未知运营商";
        };
    }

    public String getValue() {
        return value;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getFullNumber() {
        return isChinaMobile() ? value : countryCode + value.substring(countryCode.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return Objects.equals(value, phone.value) && Objects.equals(countryCode, phone.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, countryCode);
    }

    @Override
    public String toString() {
        return "Phone{" +
                "value='" + getMaskedNumber() + '\'' +
                ", countryCode='" + countryCode + '\'' +
                '}';
    }
}