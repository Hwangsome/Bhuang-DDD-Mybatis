package com.ecommerce.payment.interfaces.converter;

import com.ecommerce.payment.proto.PaymentServiceProto.*;
import com.ecommerce.common.proto.CommonProto;
import org.springframework.stereotype.Component;

/**
 * 支付Proto转换器 - 最小可编译版本
 * 暂时提供基础的转换功能
 */
@Component
public class PaymentProtoConverter {
    
    public CommonProto.ResponseStatus createSuccessStatus() {
        return CommonProto.ResponseStatus.newBuilder()
                .setCode(200)
                .setMessage("SUCCESS")
                .build();
    }
    
    public CommonProto.ResponseStatus createErrorStatus(int code, String message) {
        return CommonProto.ResponseStatus.newBuilder()
                .setCode(code)
                .setMessage(message)
                .build();
    }
}