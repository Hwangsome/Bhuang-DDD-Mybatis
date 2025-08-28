package com.ecommerce.product.interfaces.grpc;

import com.ecommerce.product.domain.entity.Product;
import com.ecommerce.product.domain.entity.ProductStatus;
import com.ecommerce.product.domain.entity.Sku;
import com.ecommerce.product.domain.repository.ProductRepository;
import com.ecommerce.product.domain.repository.SkuRepository;
import com.ecommerce.product.domain.valueobject.*;
import com.ecommerce.product.interfaces.converter.ProductProtoConverter;
import com.ecommerce.product.proto.ProductServiceGrpc;
import com.ecommerce.product.proto.ProductServiceProto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商品gRPC服务实现 - DDD应用服务层
 * 职责：处理gRPC请求，协调领域对象完成业务操作
 */
@GrpcService
public class ProductGrpcServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ProductGrpcServiceImpl.class);
    
    private final ProductRepository productRepository;
    private final SkuRepository skuRepository;
    private final ProductProtoConverter protoConverter;
    
    public ProductGrpcServiceImpl(ProductRepository productRepository, 
                                 SkuRepository skuRepository,
                                 ProductProtoConverter protoConverter) {
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
        this.protoConverter = protoConverter;
    }

    @Override
    @Transactional
    public void createProduct(ProductServiceProto.CreateProductRequest request, StreamObserver<ProductServiceProto.ProductResponse> responseObserver) {
        try {
            // 转换为领域对象
            Product product = protoConverter.fromProtoCreateRequest(request);
            
            // 保存商品
            Product savedProduct = productRepository.save(product);
            
            // 构建响应
            ProductServiceProto.ProductResponse response = ProductServiceProto.ProductResponse.newBuilder()
                    .setProduct(protoConverter.toProtoProduct(savedProduct))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.info("商品创建成功: productId={}, name={}", 
                    savedProduct.getProductId().getValue(), savedProduct.getName());
            
        } catch (IllegalArgumentException e) {
            logger.warn("商品创建参数错误: {}", e.getMessage());
            ProductServiceProto.ProductResponse response = ProductServiceProto.ProductResponse.newBuilder()
                    .setStatus(protoConverter.createErrorStatus(400, "参数错误: " + e.getMessage()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("商品创建失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("商品创建失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getProduct(ProductServiceProto.GetProductRequest request, StreamObserver<ProductServiceProto.ProductResponse> responseObserver) {
        try {
            ProductId productId = ProductId.of(request.getProductId());
            Optional<Product> productOpt = productRepository.findById(productId);
            
            if (productOpt.isEmpty()) {
                ProductServiceProto.ProductResponse response = ProductServiceProto.ProductResponse.newBuilder()
                        .setStatus(protoConverter.createErrorStatus(404, "商品不存在"))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            Product product = productOpt.get();
            ProductServiceProto.ProductResponse response = ProductServiceProto.ProductResponse.newBuilder()
                    .setProduct(protoConverter.toProtoProduct(product))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("获取商品失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("获取商品失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getProductsByIds(ProductServiceProto.GetProductsByIdsRequest request, StreamObserver<ProductServiceProto.GetProductsByIdsResponse> responseObserver) {
        try {
            List<ProductId> productIds = request.getProductIdsList().stream()
                    .map(ProductId::of)
                    .collect(Collectors.toList());
            
            List<Product> products = productRepository.findByIds(productIds);
            
            ProductServiceProto.GetProductsByIdsResponse response = ProductServiceProto.GetProductsByIdsResponse.newBuilder()
                    .addAllProducts(protoConverter.toProtoProductList(products))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("批量获取商品失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("批量获取商品失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional
    public void updateProduct(ProductServiceProto.UpdateProductRequest request, StreamObserver<ProductServiceProto.ProductResponse> responseObserver) {
        try {
            ProductId productId = ProductId.of(request.getProductId());
            Optional<Product> productOpt = productRepository.findById(productId);
            
            if (productOpt.isEmpty()) {
                ProductServiceProto.ProductResponse response = ProductServiceProto.ProductResponse.newBuilder()
                        .setStatus(protoConverter.createErrorStatus(404, "商品不存在"))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            Product product = productOpt.get();
            
            // 更新商品信息
            if (request.hasName() || request.hasDescription()) {
                String name = request.hasName() ? request.getName() : product.getName();
                String description = request.hasDescription() ? request.getDescription() : product.getDescription();
                String brandName = product.getBrandName(); // 保持原有brand
                
                product.updateBasicInfo(name, description, brandName);
            }
            
            // 更新分类
            if (request.hasCategoryId()) {
                CategoryId categoryId = CategoryId.of(request.getCategoryId());
                product.updateCategory(categoryId);
            }
            
            // 更新状态
            if (request.hasStatus()) {
                switch (request.getStatus()) {
                    case PRODUCT_ACTIVE -> product.activate();
                    case PRODUCT_INACTIVE -> product.deactivate();
                    case PRODUCT_DELETED -> product.deactivate(); // 删除状态映射为停用
                }
            }
            
            // 保存更新
            Product updatedProduct = productRepository.save(product);
            
            ProductServiceProto.ProductResponse response = ProductServiceProto.ProductResponse.newBuilder()
                    .setProduct(protoConverter.toProtoProduct(updatedProduct))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.info("商品更新成功: productId={}", productId.getValue());
            
        } catch (Exception e) {
            logger.error("商品更新失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("商品更新失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void searchProducts(ProductServiceProto.SearchProductsRequest request, StreamObserver<ProductServiceProto.SearchProductsResponse> responseObserver) {
        try {
            String keyword = request.hasKeyword() ? request.getKeyword() : null;
            CategoryId categoryId = request.hasCategoryId() ? CategoryId.of(request.getCategoryId()) : null;
            ProductStatus status = request.hasStatus() ? convertProtoStatusToDomain(request.getStatus()) : null;
            
            int pageNumber = request.getPageRequest().getPageNumber();
            int pageSize = request.getPageRequest().getPageSize();
            int offset = (pageNumber - 1) * pageSize;
            
            List<Product> products = productRepository.searchProducts(keyword, categoryId, status, offset, pageSize);
            
            ProductServiceProto.SearchProductsResponse response = ProductServiceProto.SearchProductsResponse.newBuilder()
                    .addAllProducts(protoConverter.toProtoProductList(products))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("搜索商品失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("搜索商品失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getProductSku(ProductServiceProto.GetProductSkuRequest request, StreamObserver<ProductServiceProto.ProductSkuResponse> responseObserver) {
        try {
            SkuId skuId = SkuId.of(request.getSkuId());
            Optional<Sku> skuOpt = skuRepository.findById(skuId);
            
            if (skuOpt.isEmpty()) {
                ProductServiceProto.ProductSkuResponse response = ProductServiceProto.ProductSkuResponse.newBuilder()
                        .setStatus(protoConverter.createErrorStatus(404, "SKU不存在"))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            ProductServiceProto.ProductSkuResponse response = ProductServiceProto.ProductSkuResponse.newBuilder()
                    .setSku(protoConverter.toProtoSku(skuOpt.get()))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("获取SKU信息失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("获取SKU信息失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getProductSkusByIds(ProductServiceProto.GetProductSkusByIdsRequest request, StreamObserver<ProductServiceProto.GetProductSkusByIdsResponse> responseObserver) {
        try {
            List<SkuId> skuIds = request.getSkuIdsList().stream()
                    .map(SkuId::of)
                    .collect(Collectors.toList());
                    
            List<Sku> skus = skuRepository.findByIdIn(skuIds);
            
            ProductServiceProto.GetProductSkusByIdsResponse response = ProductServiceProto.GetProductSkusByIdsResponse.newBuilder()
                    .addAllSkus(protoConverter.toProtoSkuList(skus))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("批量获取SKU信息失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("批量获取SKU信息失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Proto商品状态转换为领域商品状态
     */
    private ProductStatus convertProtoStatusToDomain(com.ecommerce.product.proto.ProductServiceProto.ProductStatus protoStatus) {
        if (protoStatus == com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_DRAFT) {
            return ProductStatus.DRAFT;
        } else if (protoStatus == com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_ACTIVE) {
            return ProductStatus.ACTIVE;
        } else if (protoStatus == com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_INACTIVE) {
            return ProductStatus.INACTIVE;
        } else if (protoStatus == com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_OUT_OF_STOCK) {
            return ProductStatus.INACTIVE; // 缺货映射到非活跃状态
        } else if (protoStatus == com.ecommerce.product.proto.ProductServiceProto.ProductStatus.PRODUCT_DELETED) {
            return ProductStatus.DELETED;
        } else {
            throw new IllegalArgumentException("未知的商品状态: " + protoStatus);
        }
    }
}