package com.ecommerce.order.interfaces.converter;

import com.ecommerce.order.proto.OrderServiceProto.*;
import com.ecommerce.common.proto.CommonProto;
import org.springframework.stereotype.Component;

/**
 * 订单Proto转换器 - 最小可编译版本
 * 暂时提供基础的转换功能
 */
@Component
public class OrderProtoConverter {
    
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