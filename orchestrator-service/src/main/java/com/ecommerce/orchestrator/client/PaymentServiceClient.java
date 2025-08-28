package com.ecommerce.orchestrator.client;

import com.ecommerce.orchestrator.config.GrpcClientConfig;
import com.ecommerce.payment.proto.PaymentServiceGrpc;
import com.ecommerce.payment.proto.PaymentServiceProto;
import com.ecommerce.common.proto.CommonProto;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 支付服务gRPC客户端
 * 职责：与支付服务进行gRPC通信，提供支付相关操作
 */
@Component
public class PaymentServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceClient.class);
    
    private final ManagedChannel channel;
    private final PaymentServiceGrpc.PaymentServiceBlockingStub blockingStub;
    
    public PaymentServiceClient(GrpcClientConfig grpcClientConfig) {
        this.channel = grpcClientConfig.paymentServiceChannel();
        this.blockingStub = PaymentServiceGrpc.newBlockingStub(channel);
    }
    
    /**
     * 创建支付订单
     */
    public PaymentServiceProto.PaymentResponse createPayment(PaymentServiceProto.CreatePaymentRequest request) {
        try {
            PaymentServiceProto.PaymentResponse response = blockingStub.createPayment(request);
            logger.debug("创建支付订单成功: paymentId={}", response.getPayment().getPaymentId());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("创建支付订单失败: error={}", e.getMessage());
            throw new RuntimeException("创建支付订单失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据ID获取支付信息
     */
    public PaymentServiceProto.Payment getPaymentById(String paymentId) {
        try {
            PaymentServiceProto.GetPaymentRequest request = PaymentServiceProto.GetPaymentRequest.newBuilder()
                    .setPaymentId(paymentId)
                    .build();
            
            PaymentServiceProto.PaymentResponse response = blockingStub.getPayment(request);
            logger.debug("获取支付信息成功: paymentId={}", paymentId);
            return response.getPayment();
            
        } catch (StatusRuntimeException e) {
            logger.error("获取支付信息失败: paymentId={}, error={}", paymentId, e.getMessage());
            throw new RuntimeException("获取支付信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量获取支付信息
     */
    public PaymentServiceProto.GetPaymentsByIdsResponse getPaymentsByIds(List<String> paymentIds) {
        try {
            PaymentServiceProto.GetPaymentsByIdsRequest request = PaymentServiceProto.GetPaymentsByIdsRequest.newBuilder()
                    .addAllPaymentIds(paymentIds)
                    .build();
            
            PaymentServiceProto.GetPaymentsByIdsResponse response = blockingStub.getPaymentsByIds(request);
            logger.debug("批量获取支付信息成功: count={}", paymentIds.size());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("批量获取支付信息失败: paymentIds={}, error={}", paymentIds, e.getMessage());
            throw new RuntimeException("批量获取支付信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 处理支付
     */
    public PaymentServiceProto.ProcessPaymentResponse processPayment(PaymentServiceProto.ProcessPaymentRequest request) {
        try {
            PaymentServiceProto.ProcessPaymentResponse response = blockingStub.processPayment(request);
            logger.debug("处理支付成功: paymentId={}, status={}", 
                       request.getPaymentId(), response.getStatus());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("处理支付失败: paymentId={}, error={}", request.getPaymentId(), e.getMessage());
            throw new RuntimeException("处理支付失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取支付状态
     */
    public PaymentServiceProto.PaymentStatusResponse getPaymentStatus(String paymentId) {
        try {
            PaymentServiceProto.GetPaymentStatusRequest request = PaymentServiceProto.GetPaymentStatusRequest.newBuilder()
                    .setPaymentId(paymentId)
                    .build();
            
            PaymentServiceProto.PaymentStatusResponse response = blockingStub.getPaymentStatus(request);
            logger.debug("获取支付状态成功: paymentId={}, status={}", paymentId, response.getStatus());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("获取支付状态失败: paymentId={}, error={}", paymentId, e.getMessage());
            throw new RuntimeException("获取支付状态失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 处理退款
     */
    public PaymentServiceProto.RefundResponse processRefund(PaymentServiceProto.ProcessRefundRequest request) {
        try {
            PaymentServiceProto.RefundResponse response = blockingStub.processRefund(request);
            logger.debug("处理退款成功: refundId={}, success={}", 
                       response.getRefund().getRefundId(), response.getStatus().getSuccess());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("处理退款失败: paymentId={}, error={}", request.getPaymentId(), e.getMessage());
            throw new RuntimeException("处理退款失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据ID获取退款信息
     */
    public PaymentServiceProto.RefundResponse getRefundById(String refundId) {
        try {
            PaymentServiceProto.GetRefundRequest request = PaymentServiceProto.GetRefundRequest.newBuilder()
                    .setRefundId(refundId)
                    .build();
            
            PaymentServiceProto.RefundResponse response = blockingStub.getRefund(request);
            logger.debug("获取退款信息成功: refundId={}", refundId);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("获取退款信息失败: refundId={}, error={}", refundId, e.getMessage());
            throw new RuntimeException("获取退款信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取支付记录
     */
    public PaymentServiceProto.GetPaymentRecordsResponse getPaymentRecords(String paymentId, 
            int pageNumber, int pageSize) {
        try {
            CommonProto.PageRequest pageRequest = CommonProto.PageRequest.newBuilder()
                    .setPageNumber(pageNumber)
                    .setPageSize(pageSize)
                    .build();
                    
            PaymentServiceProto.GetPaymentRecordsRequest.Builder requestBuilder = 
                    PaymentServiceProto.GetPaymentRecordsRequest.newBuilder()
                    .setPageRequest(pageRequest);
                    
            if (paymentId != null && !paymentId.isEmpty()) {
                requestBuilder.setPaymentId(paymentId);
            }
            
            PaymentServiceProto.GetPaymentRecordsRequest request = requestBuilder.build();
            PaymentServiceProto.GetPaymentRecordsResponse response = blockingStub.getPaymentRecords(request);
            
            logger.debug("获取支付记录成功: paymentId={}", paymentId);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("获取支付记录失败: paymentId={}, error={}", paymentId, e.getMessage());
            throw new RuntimeException("获取支付记录失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 处理支付回调
     */
    public PaymentServiceProto.PaymentCallbackResponse handlePaymentCallback(PaymentServiceProto.PaymentCallbackRequest request) {
        try {
            PaymentServiceProto.PaymentCallbackResponse response = blockingStub.handlePaymentCallback(request);
            logger.debug("处理支付回调成功: success={}", response.getSuccess());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("处理支付回调失败: error={}", e.getMessage());
            throw new RuntimeException("处理支付回调失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 健康检查
     */
    public boolean isHealthy() {
        try {
            // 使用简单的获取支付记录请求作为健康检查
            CommonProto.PageRequest pageRequest = CommonProto.PageRequest.newBuilder()
                    .setPageNumber(1)
                    .setPageSize(1)
                    .build();
                    
            PaymentServiceProto.GetPaymentRecordsRequest request = 
                    PaymentServiceProto.GetPaymentRecordsRequest.newBuilder()
                    .setPageRequest(pageRequest)
                    .build();
            
            PaymentServiceProto.GetPaymentRecordsResponse response = blockingStub.getPaymentRecords(request);
            return response.getStatus().getSuccess();
        } catch (Exception e) {
            logger.warn("支付服务健康检查失败: {}", e.getMessage());
            return false;
        }
    }
}