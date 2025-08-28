package com.ecommerce.product.interfaces.converter;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.entity.ProductStatus;
import com.ecommerce.product.domain.entity.Sku;
import com.ecommerce.product.domain.entity.SkuStatus;
import com.ecommerce.product.domain.valueobject.*;
import com.ecommerce.product.proto.ProductServiceProto.*;
import com.ecommerce.common.proto.CommonProto;
import com.google.protobuf.Timestamp;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品Proto转换器 - 接口层
 * 职责：领域对象与gRPC Proto消息之间的转换
 */
@Component
public class ProductProtoConverter {
    
    public Product fromProtoCreateRequest(CreateProductRequest request) {
        return Product.create(
                request.getName(),
                !request.getDescription().isEmpty() ? request.getDescription() : null,
                CategoryId.of(request.getCategoryId()),
                null // proto中没有brand字段，设为null
        );
    }
    
    public com.ecommerce.product.proto.ProductServiceProto.Product toProtoProduct(Product product) {
        return com.ecommerce.product.proto.ProductServiceProto.Product.newBuilder()
                .setProductId(product.getProductId().getValue())
                .setName(product.getName())
                .setDescription(product.getDescription() != null ? product.getDescription() : "")
                .setCategoryId(product.getCategoryId().getValue())
                .setStatus(toProtoProductStatus(product.getStatus()))
                .build();
    }
    
    public List<com.ecommerce.product.proto.ProductServiceProto.Product> toProtoProductList(List<Product> products) {
        return products.stream()
                .map(this::toProtoProduct)
                .collect(Collectors.toList());
    }

    
    public ProductSku toProtoSku(Sku sku) {
        return ProductSku.newBuilder()
                .setSkuId(sku.getSkuId().getValue())
                .setProductId(sku.getProductId().getValue())
                .setSkuCode(sku.getBarcode() != null ? sku.getBarcode() : "")
                .setPrice(toProtoMoney(sku.getPrice()))
                .setOriginalPrice(toProtoMoney(sku.getPrice())) // 简化处理，如果有originalPrice字段可修改
                .setImageUrl("") // SKU图片URL，如果需要可以后续添加
                .setStatus(toProtoSkuStatus(sku.getStatus()))
                .setCreatedAt(toProtoTimestamp(sku.getCreatedAt()))
                .setUpdatedAt(toProtoTimestamp(sku.getUpdatedAt()))
                .build();
    }
    
    public List<ProductSku> toProtoSkuList(List<Sku> skus) {
        return skus.stream()
                .map(this::toProtoSku)
                .collect(Collectors.toList());
    }
    
    public CommonProto.Money toProtoMoney(Money money) {
        return CommonProto.Money.newBuilder()
                .setAmount(money.getAmount().multiply(new BigDecimal("100")).longValue()) // 转换为分
                .setCurrency(money.getCurrency().getCurrencyCode())
                .build();
    }
    
    public CommonProto.Timestamp toProtoTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return CommonProto.Timestamp.newBuilder().build();
        }
        return CommonProto.Timestamp.newBuilder()
                .setSeconds(dateTime.toEpochSecond(java.time.ZoneOffset.UTC))
                .setNanos(dateTime.getNano())
                .build();
    }
    
    public CommonProto.ResponseStatus createSuccessStatus() {
        return CommonProto.ResponseStatus.newBuilder()
                .setCode(200)
                .setMessage("SUCCESS")
                .setSuccess(true)
                .build();
    }
    
    public CommonProto.ResponseStatus createErrorStatus(int code, String message) {
        return CommonProto.ResponseStatus.newBuilder()
                .setCode(code)
                .setMessage(message)
                .setSuccess(false)
                .build();
    }
    
    private com.ecommerce.product.proto.ProductServiceProto.ProductStatus toProtoProductStatus(ProductStatus status) {
        return switch (status) {
            case DRAFT -> com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_DRAFT;
            case ACTIVE -> com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_ACTIVE;
            case INACTIVE -> com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_INACTIVE;
            case DELETED -> com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_DELETED;
        };
    }
    
    // SKU使用与Product相同的状态枚举
    private com.ecommerce.product.proto.ProductServiceProto.ProductStatus toProtoSkuStatus(SkuStatus status) {
        return switch (status) {
            case ACTIVE -> com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_ACTIVE;
            case INACTIVE -> com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_INACTIVE;
            case DELETED -> com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_DELETED;
            default -> com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_INACTIVE;
        };
    }
}