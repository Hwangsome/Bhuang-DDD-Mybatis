package com.ecommerce.orchestrator.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情响应DTO - HTTP接口层响应对象
 * 职责：聚合多个服务的数据，为前端提供完整的订单详情
 * 特性：包含用户信息、商品信息、库存信息、支付信息的聚合视图
 */
public class OrderDetailResponseDto {

    // 订单基础信息
    private String orderId;
    private String orderNumber;
    private String status;
    private String type;
    private BigDecimal totalAmount;
    private BigDecimal productAmount;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private BigDecimal taxAmount;
    private String remark;
    private String couponId;

    // 时间信息
    private LocalDateTime orderTime;
    private LocalDateTime paidTime;
    private LocalDateTime shippedTime;
    private LocalDateTime deliveredTime;
    private LocalDateTime completedTime;
    private LocalDateTime cancelledTime;
    private String cancelReason;

    // 用户信息 (从用户服务获取)
    private UserInfoDto user;

    // 订单项列表 (聚合商品和库存信息)
    private List<OrderItemDetailDto> items;

    // 收货地址
    private AddressDto shippingAddress;

    // 支付信息 (从支付服务获取)
    private PaymentInfoDto payment;

    /**
     * 用户信息DTO
     */
    public static class UserInfoDto {
        private String userId;
        private String username;
        private String email;
        private String phone;
        private String fullName;
        private String type;

        // Constructors
        public UserInfoDto() {}

        public UserInfoDto(String userId, String username, String email, String phone, String fullName, String type) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.fullName = fullName;
            this.type = type;
        }

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    /**
     * 订单项详情DTO - 聚合商品和库存信息
     */
    public static class OrderItemDetailDto {
        private String orderItemId;
        private String skuId;
        private String productId;
        private String productName;
        private String skuName;
        private String imageUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private BigDecimal originalPrice;
        private BigDecimal discountAmount;
        
        // 商品规格信息
        private List<SpecificationDto> specifications;
        
        // 库存信息
        private InventoryInfoDto inventory;

        // Constructors
        public OrderItemDetailDto() {}

        // Getters and Setters
        public String getOrderItemId() { return orderItemId; }
        public void setOrderItemId(String orderItemId) { this.orderItemId = orderItemId; }

        public String getSkuId() { return skuId; }
        public void setSkuId(String skuId) { this.skuId = skuId; }

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public String getSkuName() { return skuName; }
        public void setSkuName(String skuName) { this.skuName = skuName; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

        public BigDecimal getOriginalPrice() { return originalPrice; }
        public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

        public List<SpecificationDto> getSpecifications() { return specifications; }
        public void setSpecifications(List<SpecificationDto> specifications) { this.specifications = specifications; }

        public InventoryInfoDto getInventory() { return inventory; }
        public void setInventory(InventoryInfoDto inventory) { this.inventory = inventory; }
    }

    /**
     * 规格DTO
     */
    public static class SpecificationDto {
        private String specName;
        private String specValue;

        public SpecificationDto() {}

        public SpecificationDto(String specName, String specValue) {
            this.specName = specName;
            this.specValue = specValue;
        }

        public String getSpecName() { return specName; }
        public void setSpecName(String specName) { this.specName = specName; }

        public String getSpecValue() { return specValue; }
        public void setSpecValue(String specValue) { this.specValue = specValue; }
    }

    /**
     * 库存信息DTO
     */
    public static class InventoryInfoDto {
        private Long availableQuantity;
        private Long reservedQuantity;
        private String stockLevel;

        public InventoryInfoDto() {}

        public InventoryInfoDto(Long availableQuantity, Long reservedQuantity, String stockLevel) {
            this.availableQuantity = availableQuantity;
            this.reservedQuantity = reservedQuantity;
            this.stockLevel = stockLevel;
        }

        public Long getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(Long availableQuantity) { this.availableQuantity = availableQuantity; }

        public Long getReservedQuantity() { return reservedQuantity; }
        public void setReservedQuantity(Long reservedQuantity) { this.reservedQuantity = reservedQuantity; }

        public String getStockLevel() { return stockLevel; }
        public void setStockLevel(String stockLevel) { this.stockLevel = stockLevel; }
    }

    /**
     * 地址DTO
     */
    public static class AddressDto {
        private String country;
        private String province;
        private String city;
        private String district;
        private String street;
        private String postalCode;
        private String contactName;
        private String contactPhone;
        private String fullAddress;

        // Constructors and getters/setters omitted for brevity
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

        public String getFullAddress() { return fullAddress; }
        public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    }

    /**
     * 支付信息DTO
     */
    public static class PaymentInfoDto {
        private String paymentId;
        private String paymentNumber;
        private String paymentMethod;
        private String paymentStatus;
        private BigDecimal paidAmount;
        private BigDecimal refundAmount;
        private LocalDateTime paymentTime;

        public PaymentInfoDto() {}

        // Getters and Setters
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

        public String getPaymentNumber() { return paymentNumber; }
        public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

        public BigDecimal getPaidAmount() { return paidAmount; }
        public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

        public BigDecimal getRefundAmount() { return refundAmount; }
        public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

        public LocalDateTime getPaymentTime() { return paymentTime; }
        public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }
    }

    // Main class constructors
    public OrderDetailResponseDto() {}

    // Main class getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getProductAmount() { return productAmount; }
    public void setProductAmount(BigDecimal productAmount) { this.productAmount = productAmount; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getShippingAmount() { return shippingAmount; }
    public void setShippingAmount(BigDecimal shippingAmount) { this.shippingAmount = shippingAmount; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getCouponId() { return couponId; }
    public void setCouponId(String couponId) { this.couponId = couponId; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public LocalDateTime getPaidTime() { return paidTime; }
    public void setPaidTime(LocalDateTime paidTime) { this.paidTime = paidTime; }

    public LocalDateTime getShippedTime() { return shippedTime; }
    public void setShippedTime(LocalDateTime shippedTime) { this.shippedTime = shippedTime; }

    public LocalDateTime getDeliveredTime() { return deliveredTime; }
    public void setDeliveredTime(LocalDateTime deliveredTime) { this.deliveredTime = deliveredTime; }

    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }

    public LocalDateTime getCancelledTime() { return cancelledTime; }
    public void setCancelledTime(LocalDateTime cancelledTime) { this.cancelledTime = cancelledTime; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public UserInfoDto getUser() { return user; }
    public void setUser(UserInfoDto user) { this.user = user; }

    public List<OrderItemDetailDto> getItems() { return items; }
    public void setItems(List<OrderItemDetailDto> items) { this.items = items; }

    public AddressDto getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(AddressDto shippingAddress) { this.shippingAddress = shippingAddress; }

    public PaymentInfoDto getPayment() { return payment; }
    public void setPayment(PaymentInfoDto payment) { this.payment = payment; }

    @Override
    public String toString() {
        return "OrderDetailResponseDto{" +
                "orderId='" + orderId + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", user=" + user +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}