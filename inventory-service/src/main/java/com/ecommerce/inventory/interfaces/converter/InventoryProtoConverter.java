package com.ecommerce.inventory.interfaces.converter;

import com.ecommerce.inventory.proto.InventoryServiceProto.*;
import com.ecommerce.common.proto.CommonProto;
import org.springframework.stereotype.Component;

/**
 * 库存Proto转换器 - 最小可编译版本
 * 暂时提供基础的转换功能
 */
@Component
public class InventoryProtoConverter {
    
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