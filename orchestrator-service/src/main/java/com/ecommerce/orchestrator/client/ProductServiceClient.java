package com.ecommerce.orchestrator.client;

import com.ecommerce.orchestrator.config.GrpcClientConfig;
import com.ecommerce.product.proto.ProductServiceGrpc;
import com.ecommerce.product.proto.ProductServiceProto;
import com.ecommerce.common.proto.CommonProto;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 商品服务gRPC客户端
 * 职责：与商品服务进行gRPC通信，提供商品相关操作
 */
@Component
public class ProductServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceClient.class);
    
    private final ManagedChannel channel;
    private final ProductServiceGrpc.ProductServiceBlockingStub blockingStub;
    
    public ProductServiceClient(GrpcClientConfig grpcClientConfig) {
        this.channel = grpcClientConfig.productServiceChannel();
        this.blockingStub = ProductServiceGrpc.newBlockingStub(channel);
    }
    
    /**
     * 根据ID获取商品信息
     */
    public ProductServiceProto.Product getProductById(String productId) {
        try {
            ProductServiceProto.GetProductRequest request = ProductServiceProto.GetProductRequest.newBuilder()
                    .setProductId(productId)
                    .setIncludeSkus(true)
                    .build();
            
            ProductServiceProto.ProductResponse response = blockingStub.getProduct(request);
            logger.debug("获取商品信息成功: productId={}", productId);
            return response.getProduct();
            
        } catch (StatusRuntimeException e) {
            logger.error("获取商品信息失败: productId={}, error={}", productId, e.getMessage());
            throw new RuntimeException("获取商品信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据SKU ID获取SKU信息
     */
    public ProductServiceProto.ProductSku getSkuById(String skuId) {
        try {
            ProductServiceProto.GetProductSkuRequest request = ProductServiceProto.GetProductSkuRequest.newBuilder()
                    .setSkuId(skuId)
                    .build();
            
            ProductServiceProto.ProductSkuResponse response = blockingStub.getProductSku(request);
            logger.debug("获取SKU信息成功: skuId={}", skuId);
            return response.getSku();
            
        } catch (StatusRuntimeException e) {
            logger.error("获取SKU信息失败: skuId={}, error={}", skuId, e.getMessage());
            throw new RuntimeException("获取SKU信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量获取SKU信息
     */
    public ProductServiceProto.GetProductSkusByIdsResponse batchGetSkus(java.util.List<String> skuIds) {
        try {
            ProductServiceProto.GetProductSkusByIdsRequest request = ProductServiceProto.GetProductSkusByIdsRequest.newBuilder()
                    .addAllSkuIds(skuIds)
                    .build();
            
            ProductServiceProto.GetProductSkusByIdsResponse response = blockingStub.getProductSkusByIds(request);
            logger.debug("批量获取SKU信息成功: count={}", skuIds.size());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("批量获取SKU信息失败: skuIds={}, error={}", skuIds, e.getMessage());
            throw new RuntimeException("批量获取SKU信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 验证商品是否存在且可购买
     */
    public boolean validateProductAvailability(String productId) {
        try {
            ProductServiceProto.GetProductRequest request = ProductServiceProto.GetProductRequest.newBuilder()
                    .setProductId(productId)
                    .build();
            
            ProductServiceProto.ProductResponse response = blockingStub.getProduct(request);
            ProductServiceProto.Product product = response.getProduct();
            
            boolean isAvailable = product.getStatus() == ProductServiceProto.ProductStatus.PRODUCT_ACTIVE;
            logger.debug("商品可购买性验证: productId={}, available={}", productId, isAvailable);
            return isAvailable;
            
        } catch (StatusRuntimeException e) {
            logger.error("商品可购买性验证失败: productId={}, error={}", productId, e.getMessage());
            return false; // 验证失败时返回不可购买
        }
    }
    
    /**
     * 搜索商品
     */
    public ProductServiceProto.SearchProductsResponse searchProducts(String keyword, int pageNumber, int pageSize) {
        try {
            CommonProto.PageRequest pageRequest = CommonProto.PageRequest.newBuilder()
                    .setPageNumber(pageNumber)
                    .setPageSize(pageSize)
                    .build();
                    
            ProductServiceProto.SearchProductsRequest.Builder requestBuilder = ProductServiceProto.SearchProductsRequest.newBuilder()
                    .setPageRequest(pageRequest);
                    
            if (keyword != null && !keyword.isEmpty()) {
                requestBuilder.setKeyword(keyword);
            }
            
            ProductServiceProto.SearchProductsRequest request = requestBuilder.build();
            ProductServiceProto.SearchProductsResponse response = blockingStub.searchProducts(request);
            
            logger.debug("搜索商品成功: keyword={}, page={}, size={}", keyword, pageNumber, pageSize);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("搜索商品失败: keyword={}, error={}", keyword, e.getMessage());
            throw new RuntimeException("搜索商品失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量获取商品信息
     */
    public ProductServiceProto.GetProductsByIdsResponse getProductsByIds(java.util.List<String> productIds) {
        try {
            ProductServiceProto.GetProductsByIdsRequest request = ProductServiceProto.GetProductsByIdsRequest.newBuilder()
                    .addAllProductIds(productIds)
                    .setIncludeSkus(true)
                    .build();
            
            ProductServiceProto.GetProductsByIdsResponse response = blockingStub.getProductsByIds(request);
            logger.debug("批量获取商品信息成功: count={}", productIds.size());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("批量获取商品信息失败: productIds={}, error={}", productIds, e.getMessage());
            throw new RuntimeException("批量获取商品信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取分类信息
     */
    public ProductServiceProto.GetCategoriesResponse getCategories(String parentId, boolean includeChildren) {
        try {
            ProductServiceProto.GetCategoriesRequest.Builder requestBuilder = ProductServiceProto.GetCategoriesRequest.newBuilder()
                    .setIncludeChildren(includeChildren);
                    
            if (parentId != null && !parentId.isEmpty()) {
                requestBuilder.setParentId(parentId);
            }
            
            ProductServiceProto.GetCategoriesRequest request = requestBuilder.build();
            ProductServiceProto.GetCategoriesResponse response = blockingStub.getCategories(request);
            
            logger.debug("获取分类信息成功: parentId={}, includeChildren={}", parentId, includeChildren);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("获取分类信息失败: parentId={}, error={}", parentId, e.getMessage());
            throw new RuntimeException("获取分类信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 健康检查 (如果商品服务支持的话)
     */
    public boolean isHealthy() {
        try {
            // 使用简单的获取分类请求作为健康检查
            ProductServiceProto.GetCategoriesRequest request = ProductServiceProto.GetCategoriesRequest.newBuilder()
                    .setIncludeChildren(false)
                    .build();
            
            ProductServiceProto.GetCategoriesResponse response = blockingStub.getCategories(request);
            return response.getStatus().getSuccess();
        } catch (Exception e) {
            logger.warn("商品服务健康检查失败: {}", e.getMessage());
            return false;
        }
    }
}