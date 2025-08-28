package com.ecommerce.orchestrator.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

/**
 * 创建订单请求DTO - HTTP接口层数据传输对象
 * 职责：定义创建订单的HTTP请求格式和验证规则
 * 特性：完整的JSR-303验证注解，清晰的字段语义
 */
public class CreateOrderRequestDto {

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemDto> items;

    @Valid
    @NotNull(message = "收货地址不能为空")
    private AddressDto shippingAddress;

    @Size(max = 500, message = "订单备注不能超过500个字符")
    private String remark;

    private String couponId; // 可选的优惠券ID

    /**
     * 订单项DTO
     */
    public static class OrderItemDto {
        
        @NotBlank(message = "SKU ID不能为空")
        private String skuId;

        @Min(value = 1, message = "商品数量必须大于0")
        @Max(value = 999, message = "商品数量不能超过999")
        private Integer quantity;

        // Constructors
        public OrderItemDto() {}

        public OrderItemDto(String skuId, Integer quantity) {
            this.skuId = skuId;
            this.quantity = quantity;
        }

        // Getters and Setters
        public String getSkuId() { return skuId; }
        public void setSkuId(String skuId) { this.skuId = skuId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        @Override
        public String toString() {
            return "OrderItemDto{" +
                    "skuId='" + skuId + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }

    /**
     * 地址DTO
     */
    public static class AddressDto {
        
        @NotBlank(message = "国家不能为空")
        private String country;

        @NotBlank(message = "省份不能为空")
        private String province;

        @NotBlank(message = "城市不能为空")
        private String city;

        private String district; // 区县，可选

        @NotBlank(message = "街道地址不能为空")
        @Size(max = 200, message = "街道地址不能超过200个字符")
        private String street;

        @Pattern(regexp = "^\\d{6}$", message = "邮政编码必须为6位数字")
        private String postalCode;

        @NotBlank(message = "联系人姓名不能为空")
        @Size(max = 50, message = "联系人姓名不能超过50个字符")
        private String contactName;

        @NotBlank(message = "联系人电话不能为空")
        @Pattern(regexp = "^1[3-9]\\d{9}$|^\\+[1-9]\\d{1,14}$", 
                message = "联系人电话格式不正确")
        private String contactPhone;

        // Constructors
        public AddressDto() {}

        // Getters and Setters
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }

        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

        public String getContactName() { return contactName; }
        public void setContactName(String contactName) { this.contactName = contactName; }

        public String getContactPhone() { return contactPhone; }
        public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

        @Override
        public String toString() {
            return "AddressDto{" +
                    "country='" + country + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", district='" + district + '\'' +
                    ", street='" + street + '\'' +
                    ", contactName='" + contactName + '\'' +
                    ", contactPhone='" + contactPhone + '\'' +
                    '}';
        }
    }

    // Constructors
    public CreateOrderRequestDto() {}

    public CreateOrderRequestDto(String userId, List<OrderItemDto> items, AddressDto shippingAddress) {
        this.userId = userId;
        this.items = items;
        this.shippingAddress = shippingAddress;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }

    public AddressDto getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(AddressDto shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getCouponId() { return couponId; }
    public void setCouponId(String couponId) { this.couponId = couponId; }

    @Override
    public String toString() {
        return "CreateOrderRequestDto{" +
                "userId='" + userId + '\'' +
                ", items=" + items +
                ", shippingAddress=" + shippingAddress +
                ", remark='" + remark + '\'' +
                ", couponId='" + couponId + '\'' +
                '}';
    }
}