package com.ecommerce.payment.interfaces.grpc;

import com.ecommerce.payment.proto.PaymentServiceProto.*;
import com.ecommerce.payment.proto.PaymentServiceGrpc;
import com.ecommerce.payment.domain.service.PaymentDomainService;
import com.ecommerce.payment.domain.entity.Payment;
import com.ecommerce.payment.domain.valueobject.Money;
import com.ecommerce.payment.domain.valueobject.PaymentMethod;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import com.ecommerce.payment.interfaces.converter.PaymentProtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@GrpcService
public class PaymentGrpcServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {
    
    @Autowired
    private PaymentProtoConverter converter;
    
    @Autowired
    private PaymentDomainService paymentDomainService;

    @Override
    public void createPayment(CreatePaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        try {
            // gRPC请求 -> 领域对象转换
            Money amount = Money.of(new BigDecimal(request.getAmount().getAmount()), request.getAmount().getCurrency());
            PaymentMethod paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod().name());
            
            // 调用领域服务执行业务逻辑并保存到数据库
            Payment payment = paymentDomainService.createPayment(
                request.getOrderId(),
                request.getUserId(),
                amount,
                paymentMethod
            );
            
            // 领域对象 -> gRPC响应转换
            PaymentResponse response = PaymentResponse.newBuilder()
                    .setPayment(com.ecommerce.payment.proto.PaymentServiceProto.Payment.newBuilder()
                            .setPaymentId(payment.getId().getValue())
                            .setOrderId(payment.getOrderId().getValue())
                            .setUserId(payment.getUserId().getValue())
                            .setPaymentMethod(com.ecommerce.payment.proto.PaymentServiceProto.PaymentMethod.valueOf(payment.getPaymentMethod().name()))
                            .setStatus(com.ecommerce.payment.proto.PaymentServiceProto.PaymentStatus.valueOf(payment.getStatus().name()))
                            .setAmount(com.ecommerce.common.proto.CommonProto.Money.newBuilder()
                                    .setAmount(payment.getAmount().getAmount().longValue() * 100) // 转换为分
                                    .setCurrency(payment.getAmount().getCurrency())
                                    .build())
                            .build())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getPayment(GetPaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        try {
            // 通过领域服务查询支付记录
            Optional<Payment> paymentOpt = paymentDomainService.getPayment(request.getPaymentId());
            if (paymentOpt.isEmpty()) {
                responseObserver.onError(new RuntimeException("Payment not found: " + request.getPaymentId()));
                return;
            }
            
            Payment payment = paymentOpt.get();
            
            // 领域对象 -> gRPC响应转换
            PaymentResponse response = PaymentResponse.newBuilder()
                    .setPayment(com.ecommerce.payment.proto.PaymentServiceProto.Payment.newBuilder()
                            .setPaymentId(payment.getId().getValue())
                            .setOrderId(payment.getOrderId().getValue())
                            .setUserId(payment.getUserId().getValue())
                            .setPaymentMethod(com.ecommerce.payment.proto.PaymentServiceProto.PaymentMethod.valueOf(payment.getPaymentMethod().name()))
                            .setStatus(com.ecommerce.payment.proto.PaymentServiceProto.PaymentStatus.valueOf(payment.getStatus().name()))
                            .setAmount(com.ecommerce.common.proto.CommonProto.Money.newBuilder()
                                    .setAmount(payment.getAmount().getAmount().longValue() * 100) // 转换为分
                                    .setCurrency(payment.getAmount().getCurrency())
                                    .build())
                            .build())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    // 其他方法的基础实现，稍后可以扩展
    @Override
    public void getPaymentsByIds(GetPaymentsByIdsRequest request, StreamObserver<GetPaymentsByIdsResponse> responseObserver) {
        try {
            GetPaymentsByIdsResponse response = GetPaymentsByIdsResponse.newBuilder()
                    .setStatus(converter.createSuccessStatus())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void processPayment(ProcessPaymentRequest request, StreamObserver<ProcessPaymentResponse> responseObserver) {
        try {
            // TODO: 实现支付处理逻辑
            ProcessPaymentResponse response = ProcessPaymentResponse.newBuilder()
                    .setPaymentId(request.getPaymentId())
                    .setStatus(com.ecommerce.payment.proto.PaymentServiceProto.PaymentStatus.PAYMENT_PROCESSING)
                    .setPaymentUrl("https://pay.example.com")
                    .setResponseStatus(converter.createSuccessStatus())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getPaymentStatus(GetPaymentStatusRequest request, StreamObserver<PaymentStatusResponse> responseObserver) {
        try {
            Optional<Payment> paymentOpt = paymentDomainService.getPayment(request.getPaymentId());
            if (paymentOpt.isEmpty()) {
                responseObserver.onError(new RuntimeException("Payment not found: " + request.getPaymentId()));
                return;
            }
            
            Payment payment = paymentOpt.get();
            PaymentStatusResponse response = PaymentStatusResponse.newBuilder()
                    .setPaymentId(request.getPaymentId())
                    .setStatus(com.ecommerce.payment.proto.PaymentServiceProto.PaymentStatus.valueOf(payment.getStatus().name()))
                    .setResponseStatus(converter.createSuccessStatus())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void processRefund(ProcessRefundRequest request, StreamObserver<RefundResponse> responseObserver) {
        try {
            // TODO: 实现退款逻辑
            RefundResponse response = RefundResponse.newBuilder()
                    .setRefund(Refund.newBuilder()
                            .setRefundId("REF_" + System.currentTimeMillis())
                            .setPaymentId(request.getPaymentId())
                            .setStatus(RefundStatus.REFUND_PENDING)
                            .setRefundAmount(request.getRefundAmount())
                            .setRefundReason(request.getRefundReason())
                            .build())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getRefund(GetRefundRequest request, StreamObserver<RefundResponse> responseObserver) {
        try {
            // TODO: 实现退款查询逻辑
            RefundResponse response = RefundResponse.newBuilder()
                    .setRefund(Refund.newBuilder()
                            .setRefundId(request.getRefundId())
                            .setStatus(RefundStatus.REFUND_PENDING)
                            .build())
                    .setStatus(converter.createSuccessStatus())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getPaymentRecords(GetPaymentRecordsRequest request, StreamObserver<GetPaymentRecordsResponse> responseObserver) {
        try {
            // TODO: 实现支付记录查询
            GetPaymentRecordsResponse response = GetPaymentRecordsResponse.newBuilder()
                    .setStatus(converter.createSuccessStatus())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void handlePaymentCallback(PaymentCallbackRequest request, StreamObserver<PaymentCallbackResponse> responseObserver) {
        try {
            // TODO: 实现支付回调处理
            PaymentCallbackResponse response = PaymentCallbackResponse.newBuilder()
                    .setSuccess(true)
                    .setResponseBody("OK")
                    .setStatus(converter.createSuccessStatus())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}