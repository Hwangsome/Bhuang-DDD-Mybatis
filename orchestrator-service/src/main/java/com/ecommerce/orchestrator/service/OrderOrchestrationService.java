package com.ecommerce.orchestrator.service;

import com.ecommerce.orchestrator.client.*;
import com.ecommerce.orchestrator.dto.request.CreateOrderRequestDto;
import com.ecommerce.orchestrator.dto.response.OrderDetailResponseDto;
import com.ecommerce.orchestrator.exception.OrchestrationException;
import com.ecommerce.common.proto.CommonProto;
import com.ecommerce.user.proto.UserServiceProto.User;
import com.ecommerce.product.proto.ProductServiceProto.*;
import com.ecommerce.inventory.proto.InventoryServiceProto.*;
import com.ecommerce.order.proto.OrderServiceProto.*;
import com.ecommerce.payment.proto.PaymentServiceProto.*;
import com.ecommerce.notification.proto.NotificationServiceProto.*;

import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单编排服务 - 复杂业务流程协调核心
 * 职责：编排订单创建的完整业务流程，协调多个微服务完成复杂业务操作
 * 特性：分布式事务、并行调用、异常补偿、业务规则验证
 */
@Service
public class OrderOrchestrationService {

    private static final Logger logger = LoggerFactory.getLogger(OrderOrchestrationService.class);
    
    private final UserServiceClient userServiceClient;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final NotificationServiceClient notificationServiceClient;
    
    public OrderOrchestrationService(
            UserServiceClient userServiceClient,
            ProductServiceClient productServiceClient,
            InventoryServiceClient inventoryServiceClient,
            OrderServiceClient orderServiceClient,
            PaymentServiceClient paymentServiceClient,
            NotificationServiceClient notificationServiceClient) {
        this.userServiceClient = userServiceClient;
        this.productServiceClient = productServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
        this.orderServiceClient = orderServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    /**
     * 创建订单 - 复杂业务编排的核心流程
     * 
     * 业务流程：
     * 1. 验证用户信息
     * 2. 验证商品信息和价格
     * 3. 检查库存并预占
     * 4. 计算订单金额（含优惠、运费、税费）
     * 5. 创建订单
     * 6. 创建支付订单
     * 7. 发送通知
     * 8. 异常时执行补偿逻辑
     */
    @GlobalTransactional(rollbackFor = Exception.class, timeoutMills = 60000)
    public OrderDetailResponseDto createOrder(CreateOrderRequestDto request) {
        logger.info("开始创建订单: userId={}, items={}", request.getUserId(), request.getItems().size());
        
        try {
            // Step 1: 验证用户信息
            User user = validateUser(request.getUserId());
            
            // Step 2: 验证商品信息（并行获取）
            List<ProductValidationResult> productResults = validateProducts(request.getItems());
            
            // Step 3: 检查库存并预占
            List<InventoryReservationResult> inventoryResults = reserveInventory(request.getItems(), user.getUserId());
            
            // Step 4: 计算订单金额
            OrderPriceCalculation priceCalculation = calculateOrderPrice(productResults, request);
            
            // Step 5: 创建订单
            Order order = createOrderRecord(user, productResults, inventoryResults, 
                                          priceCalculation, request);
            
            // Step 6: 创建支付订单
            Payment payment = createPaymentRecord(order, user);
            
            // Step 7: 发送订单创建通知（异步）
            sendOrderCreatedNotification(order, user);
            
            // Step 8: 构建详情响应
            OrderDetailResponseDto response = buildOrderDetailResponse(order, user, 
                                                                     productResults, inventoryResults, payment);
            
            logger.info("订单创建成功: orderId={}, orderNumber={}, totalAmount={}", 
                       order.getOrderId(), order.getOrderNumber(), order.getTotalAmount());
            
            return response;
            
        } catch (Exception e) {
            logger.error("订单创建失败: userId={}, error={}", request.getUserId(), e.getMessage(), e);
            // 分布式事务会自动回滚，包括已预占的库存
            throw new OrchestrationException("订单创建失败: " + e.getMessage(), e);
        }
    }

    /**
     * Step 1: 验证用户信息
     */
    private User validateUser(String userId) {
        logger.debug("验证用户信息: userId={}", userId);
        
        Optional<User> userOpt = userServiceClient.getUser(userId);
        if (userOpt.isEmpty()) {
            throw new OrchestrationException("用户不存在: " + userId);
        }
        
        User user = userOpt.get();
        if (!"USER_ACTIVE".equals(user.getStatus().name())) {
            throw new OrchestrationException("用户状态异常，无法创建订单: " + user.getStatus().name());
        }
        
        logger.debug("用户验证通过: userId={}, username={}", user.getUserId(), user.getUsername());
        return user;
    }

    /**
     * Step 2: 验证商品信息（并行调用优化）
     */
    private List<ProductValidationResult> validateProducts(List<CreateOrderRequestDto.OrderItemDto> items) {
        logger.debug("验证商品信息: items={}", items.size());
        
        // 提取所有SKU ID
        List<String> skuIds = items.stream()
                .map(CreateOrderRequestDto.OrderItemDto::getSkuId)
                .collect(Collectors.toList());
        
        // 并行获取SKU信息
        CompletableFuture<List<com.ecommerce.product.proto.ProductServiceProto.ProductSku>> skusFuture = CompletableFuture.supplyAsync(() -> {
            return productServiceClient.batchGetSkus(skuIds).getSkusList();
        });
        
        // 等待并处理结果
        List<com.ecommerce.product.proto.ProductServiceProto.ProductSku> skus;
        try {
            skus = skusFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new OrchestrationException("获取商品信息失败", e);
        }
        
        // 验证所有商品都存在且有效
        Map<String, com.ecommerce.product.proto.ProductServiceProto.ProductSku> skuMap = skus.stream()
                .collect(Collectors.toMap(com.ecommerce.product.proto.ProductServiceProto.ProductSku::getSkuId, sku -> sku));
        
        List<ProductValidationResult> results = new ArrayList<>();
        
        for (CreateOrderRequestDto.OrderItemDto item : items) {
            com.ecommerce.product.proto.ProductServiceProto.ProductSku sku = skuMap.get(item.getSkuId());
            if (sku == null) {
                throw new OrchestrationException("商品不存在: skuId=" + item.getSkuId());
            }
            
            if (sku.getStatus() != com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_ACTIVE) {
                throw new OrchestrationException("商品状态异常: skuId=" + item.getSkuId() + 
                                          ", status=" + sku.getStatus().name());
            }
            
            results.add(new ProductValidationResult(item.getSkuId(), item.getQuantity(), sku));
        }
        
        logger.debug("商品验证通过: validProducts={}", results.size());
        return results;
    }

    /**
     * Step 3: 检查库存并预占
     */
    private List<InventoryReservationResult> reserveInventory(
            List<CreateOrderRequestDto.OrderItemDto> items, String userId) {
        logger.debug("开始库存预占: items={}", items.size());
        
        List<InventoryReservationResult> results = new ArrayList<>();
        
        for (CreateOrderRequestDto.OrderItemDto item : items) {
            // 检查库存
            Optional<Inventory> inventoryOpt = inventoryServiceClient.getInventory(item.getSkuId());
            if (inventoryOpt.isEmpty()) {
                throw new OrchestrationException("库存信息不存在: skuId=" + item.getSkuId());
            }
            
            Inventory inventory = inventoryOpt.get();
            if (inventory.getAvailableQuantity() < item.getQuantity()) {
                throw new OrchestrationException("库存不足: skuId=" + item.getSkuId() + 
                                          ", available=" + inventory.getAvailableQuantity() + 
                                          ", required=" + item.getQuantity());
            }
            
            // 预占库存
            boolean reserved = inventoryServiceClient.reserveStock(
                    item.getSkuId(), item.getQuantity(), "ORDER_CREATE", userId);
            
            if (!reserved) {
                throw new OrchestrationException("库存预占失败: skuId=" + item.getSkuId());
            }
            
            results.add(new InventoryReservationResult(item.getSkuId(), item.getQuantity(), inventory));
        }
        
        logger.debug("库存预占成功: reservedItems={}", results.size());
        return results;
    }

    /**
     * Step 4: 计算订单价格
     */
    private OrderPriceCalculation calculateOrderPrice(
            List<ProductValidationResult> productResults, CreateOrderRequestDto request) {
        logger.debug("计算订单价格");
        
        BigDecimal productAmount = BigDecimal.ZERO;
        BigDecimal originalAmount = BigDecimal.ZERO;
        
        // 计算商品总金额
        for (ProductValidationResult result : productResults) {
            BigDecimal price = convertMoneyToBigDecimal(result.getSku().getPrice());
            BigDecimal originalPrice = convertMoneyToBigDecimal(result.getSku().getOriginalPrice());
            BigDecimal quantity = BigDecimal.valueOf(result.getQuantity());
            
            productAmount = productAmount.add(price.multiply(quantity));
            originalAmount = originalAmount.add(originalPrice.multiply(quantity));
        }
        
        // 计算优惠金额
        BigDecimal discountAmount = calculateDiscount(request.getCouponId(), productAmount);
        
        // 计算运费 (简化版: 固定运费或免费)
        BigDecimal shippingAmount = calculateShipping(productAmount, discountAmount);
        
        // 计算税费 (简化版: 0)
        BigDecimal taxAmount = BigDecimal.ZERO;
        
        // 计算总金额
        BigDecimal totalAmount = productAmount
                .subtract(discountAmount)
                .add(shippingAmount)
                .add(taxAmount);
        
        OrderPriceCalculation calculation = new OrderPriceCalculation(
                productAmount, originalAmount, discountAmount, shippingAmount, taxAmount, totalAmount);
        
        logger.debug("订单价格计算完成: totalAmount={}, productAmount={}, discountAmount={}", 
                    totalAmount, productAmount, discountAmount);
        
        return calculation;
    }

    /**
     * Step 5: 创建订单记录
     */
    private Order createOrderRecord(User user, List<ProductValidationResult> productResults,
                                  List<InventoryReservationResult> inventoryResults,
                                  OrderPriceCalculation priceCalculation, 
                                  CreateOrderRequestDto request) {
        logger.debug("创建订单记录: userId={}", user.getUserId());
        
        // 构建订单项
        List<CreateOrderItemRequest> orderItems = productResults.stream()
                .map(result -> {
                    com.ecommerce.product.proto.ProductServiceProto.ProductSku sku = result.getSku();
                    return CreateOrderItemRequest.newBuilder()
                            .setSkuId(sku.getSkuId())
                            .setQuantity(result.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());
        
        // 构建地址信息
        CommonProto.Address address = buildProtoAddress(request.getShippingAddress());
        
        // 创建订单请求
        CreateOrderRequest orderRequest = CreateOrderRequest.newBuilder()
                .setUserId(user.getUserId())
                .addAllItems(orderItems)
                .setShippingAddress(address)
                .setRemark(request.getRemark() != null ? request.getRemark() : "")
                .setCouponId(request.getCouponId() != null ? request.getCouponId() : "")
                .setType(OrderType.NORMAL_ORDER)
                .build();
        
        OrderResponse orderResponse = orderServiceClient.createOrder(orderRequest);
        if (!orderResponse.getStatus().getSuccess()) {
            throw new OrchestrationException("创建订单失败: " + orderResponse.getStatus().getMessage());
        }
        
        Order order = orderResponse.getOrder();
        logger.debug("订单创建成功: orderId={}, orderNumber={}", order.getOrderId(), order.getOrderNumber());
        
        return order;
    }

    /**
     * Step 6: 创建支付订单
     */
    private Payment createPaymentRecord(Order order, User user) {
        logger.debug("创建支付订单: orderId={}", order.getOrderId());
        
        CreatePaymentRequest paymentRequest = CreatePaymentRequest.newBuilder()
                .setOrderId(order.getOrderId())
                .setUserId(user.getUserId())
                .setPaymentMethod(PaymentMethod.ALIPAY) // 默认支付宝
                .setAmount(order.getTotalAmount())
                .setExpireMinutes(30) // 30分钟过期
                .setDescription("电商订单支付")
                .build();
        
        PaymentResponse paymentResponse = paymentServiceClient.createPayment(paymentRequest);
        if (!paymentResponse.getStatus().getSuccess()) {
            throw new OrchestrationException("创建支付订单失败: " + paymentResponse.getStatus().getMessage());
        }
        
        Payment payment = paymentResponse.getPayment();
        logger.debug("支付订单创建成功: paymentId={}", payment.getPaymentId());
        
        return payment;
    }

    /**
     * Step 7: 发送通知（异步）
     */
    private void sendOrderCreatedNotification(Order order, User user) {
        CompletableFuture.runAsync(() -> {
            try {
                logger.debug("发送订单创建通知: orderId={}, userId={}", order.getOrderId(), user.getUserId());
                
                // 构建通知内容
                Map<String, String> templateParams = Map.of(
                        "orderNumber", order.getOrderNumber(),
                        "totalAmount", order.getTotalAmount().toString(),
                        "userName", user.getFirstName() + " " + user.getLastName()
                );
                
                // 发送邮件通知
                notificationServiceClient.sendEmail(
                        user.getUserId(),
                        user.getEmail(),
                        "订单创建成功",
                        "您的订单 " + order.getOrderNumber() + " 已创建成功，总金额：" + order.getTotalAmount()
                );
                
                // 发送短信通知
                notificationServiceClient.sendSMS(
                        user.getUserId(),
                        user.getPhone(),
                        "您的订单 " + order.getOrderNumber() + " 已创建成功"
                );
                
                logger.debug("订单创建通知发送成功: orderId={}", order.getOrderId());
                
            } catch (Exception e) {
                logger.warn("订单创建通知发送失败: orderId={}, error={}", order.getOrderId(), e.getMessage());
                // 通知发送失败不影响订单创建流程
            }
        });
    }

    // 辅助方法和内部类定义
    private CommonProto.Address buildProtoAddress(CreateOrderRequestDto.AddressDto addressDto) {
        return CommonProto.Address.newBuilder()
                .setCountry(addressDto.getCountry())
                .setProvince(addressDto.getProvince())
                .setCity(addressDto.getCity())
                .setDistrict(addressDto.getDistrict() != null ? addressDto.getDistrict() : "")
                .setStreet(addressDto.getStreet())
                .setPostalCode(addressDto.getPostalCode() != null ? addressDto.getPostalCode() : "")
                .setContactName(addressDto.getContactName())
                .setContactPhone(addressDto.getContactPhone())
                .build();
    }

    private BigDecimal convertMoneyToBigDecimal(CommonProto.Money money) {
        return BigDecimal.valueOf(money.getAmount()).divide(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateDiscount(String couponId, BigDecimal productAmount) {
        if (couponId == null || couponId.isEmpty()) {
            return BigDecimal.ZERO;
        }
        // 简化版: 固定10%折扣
        return productAmount.multiply(BigDecimal.valueOf(0.1));
    }

    private BigDecimal calculateShipping(BigDecimal productAmount, BigDecimal discountAmount) {
        BigDecimal finalAmount = productAmount.subtract(discountAmount);
        // 简化版: 满99免运费，否则10元运费
        return finalAmount.compareTo(BigDecimal.valueOf(99)) >= 0 ? 
               BigDecimal.ZERO : BigDecimal.valueOf(10);
    }

    private OrderDetailResponseDto buildOrderDetailResponse(Order order, User user,
                                                           List<ProductValidationResult> productResults,
                                                           List<InventoryReservationResult> inventoryResults,
                                                           Payment payment) {
        // 实现订单详情响应构建逻辑
        // 这里简化实现，实际项目中需要详细的对象转换
        OrderDetailResponseDto response = new OrderDetailResponseDto();
        response.setOrderId(order.getOrderId());
        response.setOrderNumber(order.getOrderNumber());
        response.setStatus(order.getStatus().name());
        response.setTotalAmount(convertMoneyToBigDecimal(order.getTotalAmount()));
        // ... 其他字段设置
        
        return response;
    }

    // 内部数据类
    private static class ProductValidationResult {
        private final String skuId;
        private final Integer quantity;
        private final com.ecommerce.product.proto.ProductServiceProto.ProductSku sku;

        public ProductValidationResult(String skuId, Integer quantity, com.ecommerce.product.proto.ProductServiceProto.ProductSku sku) {
            this.skuId = skuId;
            this.quantity = quantity;
            this.sku = sku;
        }

        public String getSkuId() { return skuId; }
        public Integer getQuantity() { return quantity; }
        public com.ecommerce.product.proto.ProductServiceProto.ProductSku getSku() { return sku; }
    }

    private static class InventoryReservationResult {
        private final String skuId;
        private final Integer reservedQuantity;
        private final Inventory inventory;

        public InventoryReservationResult(String skuId, Integer reservedQuantity, Inventory inventory) {
            this.skuId = skuId;
            this.reservedQuantity = reservedQuantity;
            this.inventory = inventory;
        }

        public String getSkuId() { return skuId; }
        public Integer getReservedQuantity() { return reservedQuantity; }
        public Inventory getInventory() { return inventory; }
    }

    private static class OrderPriceCalculation {
        private final BigDecimal productAmount;
        private final BigDecimal originalAmount;
        private final BigDecimal discountAmount;
        private final BigDecimal shippingAmount;
        private final BigDecimal taxAmount;
        private final BigDecimal totalAmount;

        public OrderPriceCalculation(BigDecimal productAmount, BigDecimal originalAmount,
                                   BigDecimal discountAmount, BigDecimal shippingAmount,
                                   BigDecimal taxAmount, BigDecimal totalAmount) {
            this.productAmount = productAmount;
            this.originalAmount = originalAmount;
            this.discountAmount = discountAmount;
            this.shippingAmount = shippingAmount;
            this.taxAmount = taxAmount;
            this.totalAmount = totalAmount;
        }

        // Getters
        public BigDecimal getProductAmount() { return productAmount; }
        public BigDecimal getOriginalAmount() { return originalAmount; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public BigDecimal getShippingAmount() { return shippingAmount; }
        public BigDecimal getTaxAmount() { return taxAmount; }
        public BigDecimal getTotalAmount() { return totalAmount; }
    }
}