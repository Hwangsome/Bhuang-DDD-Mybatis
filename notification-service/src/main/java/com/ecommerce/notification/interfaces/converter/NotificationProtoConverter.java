package com.ecommerce.notification.interfaces.converter;

import com.ecommerce.notification.proto.NotificationServiceProto.*;
import com.ecommerce.common.proto.CommonProto;
import org.springframework.stereotype.Component;

/**
 * 通知Proto转换器 - 最小可编译版本
 * 暂时提供基础的转换功能
 */
@Component
public class NotificationProtoConverter {
    
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