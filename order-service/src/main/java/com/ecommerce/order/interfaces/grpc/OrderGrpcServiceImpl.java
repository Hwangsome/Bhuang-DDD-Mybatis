package com.ecommerce.order.interfaces.grpc;

import com.ecommerce.order.proto.OrderServiceGrpc;
import com.ecommerce.order.proto.OrderServiceProto.*;
import com.ecommerce.common.proto.CommonProto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 订单gRPC服务实现 - 最小可编译版本
 * 暂时返回默认响应，后续需要完善业务逻辑
 */
@GrpcService
public class OrderGrpcServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(OrderGrpcServiceImpl.class);

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            OrderResponse response = OrderResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("订单创建失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("订单创建失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            OrderResponse response = OrderResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("获取订单失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("获取订单失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getOrdersByIds(GetOrdersByIdsRequest request, StreamObserver<GetOrdersByIdsResponse> responseObserver) {
        try {
            GetOrdersByIdsResponse response = GetOrdersByIdsResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("批量获取订单失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("批量获取订单失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getUserOrders(GetUserOrdersRequest request, StreamObserver<GetUserOrdersResponse> responseObserver) {
        try {
            GetUserOrdersResponse response = GetUserOrdersResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("获取用户订单失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("获取用户订单失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateOrderStatus(UpdateOrderStatusRequest request, StreamObserver<OrderResponse> responseObserver) {
        try {
            OrderResponse response = OrderResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("更新订单状态失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("更新订单状态失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void cancelOrder(CancelOrderRequest request, StreamObserver<CancelOrderResponse> responseObserver) {
        try {
            CancelOrderResponse response = CancelOrderResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("取消订单失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("取消订单失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void searchOrders(SearchOrdersRequest request, StreamObserver<SearchOrdersResponse> responseObserver) {
        try {
            SearchOrdersResponse response = SearchOrdersResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("搜索订单失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("搜索订单失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getOrderStatistics(GetOrderStatisticsRequest request, StreamObserver<GetOrderStatisticsResponse> responseObserver) {
        try {
            GetOrderStatisticsResponse response = GetOrderStatisticsResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("获取订单统计失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("获取订单统计失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private CommonProto.ResponseStatus createSuccessStatus() {
        return CommonProto.ResponseStatus.newBuilder()
                .setCode(200)
                .setMessage("SUCCESS")
                .build();
    }
}