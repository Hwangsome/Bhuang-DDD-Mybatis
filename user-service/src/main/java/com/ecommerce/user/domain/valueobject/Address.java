package com.ecommerce.user.domain.valueobject;

import java.util.Objects;

/**
 * 地址值对象 - 封装地址信息和验证逻辑
 * DDD值对象特征：聚合多个相关属性，提供业务行为
 */
public final class Address {
    
    private final String country;
    private final String province;
    private final String city;
    private final String district;
    private final String street;
    private final String postalCode;
    private final String contactName;
    private final String contactPhone;

    private Address(Builder builder) {
        this.country = Objects.requireNonNull(builder.country, "国家不能为空");
        this.province = Objects.requireNonNull(builder.province, "省份不能为空");
        this.city = Objects.requireNonNull(builder.city, "城市不能为空");
        this.district = builder.district;
        this.street = Objects.requireNonNull(builder.street, "街道地址不能为空");
        this.postalCode = builder.postalCode;
        this.contactName = Objects.requireNonNull(builder.contactName, "联系人姓名不能为空");
        this.contactPhone = Objects.requireNonNull(builder.contactPhone, "联系人电话不能为空");
        
        validateAddress();
    }

    /**
     * 创建地址构建器
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 验证地址信息
     */
    private void validateAddress() {
        if (country.trim().isEmpty()) {
            throw new IllegalArgumentException("国家不能为空");
        }
        if (province.trim().isEmpty()) {
            throw new IllegalArgumentException("省份不能为空");
        }
        if (city.trim().isEmpty()) {
            throw new IllegalArgumentException("城市不能为空");
        }
        if (street.trim().isEmpty()) {
            throw new IllegalArgumentException("街道地址不能为空");
        }
        if (contactName.trim().isEmpty()) {
            throw new IllegalArgumentException("联系人姓名不能为空");
        }
        if (contactPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("联系人电话不能为空");
        }
        
        // 验证邮政编码格式 (中国)
        if ("中国".equals(country) && postalCode != null) {
            if (!postalCode.matches("\\d{6}")) {
                throw new IllegalArgumentException("中国邮政编码必须为6位数字");
            }
        }
    }

    /**
     * 获取完整地址字符串
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(country);
        sb.append(" ").append(province);
        sb.append(" ").append(city);
        if (district != null && !district.trim().isEmpty()) {
            sb.append(" ").append(district);
        }
        sb.append(" ").append(street);
        return sb.toString();
    }

    /**
     * 获取简化地址 (不包含国家)
     */
    public String getShortAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(province);
        sb.append(" ").append(city);
        if (district != null && !district.trim().isEmpty()) {
            sb.append(" ").append(district);
        }
        sb.append(" ").append(street);
        return sb.toString();
    }

    /**
     * 判断是否为中国地址
     */
    public boolean isChinaAddress() {
        return "中国".equals(country) || "China".equalsIgnoreCase(country);
    }

    /**
     * 判断是否为同城地址
     */
    public boolean isSameCity(Address other) {
        return Objects.equals(this.country, other.country) &&
               Objects.equals(this.province, other.province) &&
               Objects.equals(this.city, other.city);
    }

    // Getters
    public String getCountry() { return country; }
    public String getProvince() { return province; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public String getStreet() { return street; }
    public String getPostalCode() { return postalCode; }
    public String getContactName() { return contactName; }
    public String getContactPhone() { return contactPhone; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(country, address.country) &&
                Objects.equals(province, address.province) &&
                Objects.equals(city, address.city) &&
                Objects.equals(district, address.district) &&
                Objects.equals(street, address.street) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(contactName, address.contactName) &&
                Objects.equals(contactPhone, address.contactPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, province, city, district, street, 
                           postalCode, contactName, contactPhone);
    }

    @Override
    public String toString() {
        return "Address{" +
                "fullAddress='" + getFullAddress() + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                '}';
    }

    /**
     * 地址构建器
     */
    public static class Builder {
        private String country;
        private String province;
        private String city;
        private String district;
        private String street;
        private String postalCode;
        private String contactName;
        private String contactPhone;

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder province(String province) {
            this.province = province;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder district(String district) {
            this.district = district;
            return this;
        }

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder contactName(String contactName) {
            this.contactName = contactName;
            return this;
        }

        public Builder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}