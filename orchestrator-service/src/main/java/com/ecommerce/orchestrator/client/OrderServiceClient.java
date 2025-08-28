package com.ecommerce.orchestrator.client;

import com.ecommerce.orchestrator.config.GrpcClientConfig;
import com.ecommerce.order.proto.OrderServiceGrpc;
import com.ecommerce.order.proto.OrderServiceProto;
import com.ecommerce.common.proto.CommonProto;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单服务gRPC客户端
 * 职责：与订单服务进行gRPC通信，提供订单相关操作
 */
@Component
public class OrderServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceClient.class);
    
    private final ManagedChannel channel;
    private final OrderServiceGrpc.OrderServiceBlockingStub blockingStub;
    
    public OrderServiceClient(GrpcClientConfig grpcClientConfig) {
        this.channel = grpcClientConfig.orderServiceChannel();
        this.blockingStub = OrderServiceGrpc.newBlockingStub(channel);
    }
    
    /**
     * 创建订单
     */
    public OrderServiceProto.OrderResponse createOrder(OrderServiceProto.CreateOrderRequest request) {
        try {
            OrderServiceProto.OrderResponse response = blockingStub.createOrder(request);
            logger.debug("创建订单成功: orderId={}", response.getOrder().getOrderId());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("创建订单失败: error={}", e.getMessage());
            throw new RuntimeException("创建订单失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据ID获取订单信息
     */
    public OrderServiceProto.Order getOrderById(String orderId) {
        try {
            OrderServiceProto.GetOrderRequest request = OrderServiceProto.GetOrderRequest.newBuilder()
                    .setOrderId(orderId)
                    .build();
            
            OrderServiceProto.OrderResponse response = blockingStub.getOrder(request);
            logger.debug("获取订单信息成功: orderId={}", orderId);
            return response.getOrder();
            
        } catch (StatusRuntimeException e) {
            logger.error("获取订单信息失败: orderId={}, error={}", orderId, e.getMessage());
            throw new RuntimeException("获取订单信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量获取订单信息
     */
    public OrderServiceProto.GetOrdersByIdsResponse getOrdersByIds(List<String> orderIds) {
        try {
            OrderServiceProto.GetOrdersByIdsRequest request = OrderServiceProto.GetOrdersByIdsRequest.newBuilder()
                    .addAllOrderIds(orderIds)
                    .build();
            
            OrderServiceProto.GetOrdersByIdsResponse response = blockingStub.getOrdersByIds(request);
            logger.debug("批量获取订单信息成功: count={}", orderIds.size());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("批量获取订单信息失败: orderIds={}, error={}", orderIds, e.getMessage());
            throw new RuntimeException("批量获取订单信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取用户订单列表
     */
    public OrderServiceProto.GetUserOrdersResponse getUserOrders(String userId, int pageNumber, int pageSize) {
        try {
            CommonProto.PageRequest pageRequest = CommonProto.PageRequest.newBuilder()
                    .setPageNumber(pageNumber)
                    .setPageSize(pageSize)
                    .build();
                    
            OrderServiceProto.GetUserOrdersRequest request = OrderServiceProto.GetUserOrdersRequest.newBuilder()
                    .setUserId(userId)
                    .setPageRequest(pageRequest)
                    .build();
            
            OrderServiceProto.GetUserOrdersResponse response = blockingStub.getUserOrders(request);
            logger.debug("获取用户订单列表成功: userId={}", userId);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("获取用户订单列表失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取用户订单列表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新订单状态
     */
    public OrderServiceProto.OrderResponse updateOrderStatus(String orderId, OrderServiceProto.OrderStatus status, String remark) {
        try {
            OrderServiceProto.UpdateOrderStatusRequest.Builder requestBuilder = OrderServiceProto.UpdateOrderStatusRequest.newBuilder()
                    .setOrderId(orderId)
                    .setStatus(status);
                    
            if (remark != null && !remark.isEmpty()) {
                requestBuilder.setRemark(remark);
            }
            
            OrderServiceProto.UpdateOrderStatusRequest request = requestBuilder.build();
            OrderServiceProto.OrderResponse response = blockingStub.updateOrderStatus(request);
            
            logger.debug("更新订单状态成功: orderId={}, status={}", orderId, status);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("更新订单状态失败: orderId={}, status={}, error={}", orderId, status, e.getMessage());
            throw new RuntimeException("更新订单状态失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 取消订单
     */
    public OrderServiceProto.CancelOrderResponse cancelOrder(String orderId, String cancelReason) {
        try {
            OrderServiceProto.CancelOrderRequest.Builder requestBuilder = OrderServiceProto.CancelOrderRequest.newBuilder()
                    .setOrderId(orderId);
                    
            if (cancelReason != null && !cancelReason.isEmpty()) {
                requestBuilder.setCancelReason(cancelReason);
            }
            
            OrderServiceProto.CancelOrderRequest request = requestBuilder.build();
            OrderServiceProto.CancelOrderResponse response = blockingStub.cancelOrder(request);
            
            logger.debug("取消订单成功: orderId={}", orderId);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("取消订单失败: orderId={}, error={}", orderId, e.getMessage());
            throw new RuntimeException("取消订单失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 搜索订单
     */
    public OrderServiceProto.SearchOrdersResponse searchOrders(String userId, OrderServiceProto.OrderStatus status, 
            int pageNumber, int pageSize) {
        try {
            CommonProto.PageRequest pageRequest = CommonProto.PageRequest.newBuilder()
                    .setPageNumber(pageNumber)
                    .setPageSize(pageSize)
                    .build();
                    
            OrderServiceProto.SearchOrdersRequest.Builder requestBuilder = OrderServiceProto.SearchOrdersRequest.newBuilder()
                    .setPageRequest(pageRequest);
                    
            if (userId != null && !userId.isEmpty()) {
                requestBuilder.setUserId(userId);
            }
            
            if (status != OrderServiceProto.OrderStatus.ORDER_STATUS_UNSPECIFIED) {
                requestBuilder.setStatus(status);
            }
            
            OrderServiceProto.SearchOrdersRequest request = requestBuilder.build();
            OrderServiceProto.SearchOrdersResponse response = blockingStub.searchOrders(request);
            
            logger.debug("搜索订单成功: userId={}, status={}", userId, status);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("搜索订单失败: userId={}, status={}, error={}", userId, status, e.getMessage());
            throw new RuntimeException("搜索订单失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取订单统计
     */
    public OrderServiceProto.GetOrderStatisticsResponse getOrderStatistics(String userId) {
        try {
            OrderServiceProto.GetOrderStatisticsRequest.Builder requestBuilder = OrderServiceProto.GetOrderStatisticsRequest.newBuilder();
                    
            if (userId != null && !userId.isEmpty()) {
                requestBuilder.setUserId(userId);
            }
            
            OrderServiceProto.GetOrderStatisticsRequest request = requestBuilder.build();
            OrderServiceProto.GetOrderStatisticsResponse response = blockingStub.getOrderStatistics(request);
            
            logger.debug("获取订单统计成功: userId={}", userId);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("获取订单统计失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取订单统计失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 健康检查
     */
    public boolean isHealthy() {
        try {
            // 使用简单的搜索订单请求作为健康检查
            CommonProto.PageRequest pageRequest = CommonProto.PageRequest.newBuilder()
                    .setPageNumber(1)
                    .setPageSize(1)
                    .build();
                    
            OrderServiceProto.SearchOrdersRequest request = OrderServiceProto.SearchOrdersRequest.newBuilder()
                    .setPageRequest(pageRequest)
                    .build();
            
            OrderServiceProto.SearchOrdersResponse response = blockingStub.searchOrders(request);
            return response.getStatus().getSuccess();
        } catch (Exception e) {
            logger.warn("订单服务健康检查失败: {}", e.getMessage());
            return false;
        }
    }
}