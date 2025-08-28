package com.ecommerce.orchestrator.client;

import com.ecommerce.orchestrator.config.GrpcClientConfig;
import com.ecommerce.inventory.proto.InventoryServiceGrpc;
import com.ecommerce.inventory.proto.InventoryServiceProto;
import com.ecommerce.common.proto.CommonProto;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 库存服务gRPC客户端
 * 职责：与库存服务进行gRPC通信，提供库存相关操作
 */
@Component
public class InventoryServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceClient.class);
    
    private final ManagedChannel channel;
    private final InventoryServiceGrpc.InventoryServiceBlockingStub blockingStub;
    
    public InventoryServiceClient(GrpcClientConfig grpcClientConfig) {
        this.channel = grpcClientConfig.inventoryServiceChannel();
        this.blockingStub = InventoryServiceGrpc.newBlockingStub(channel);
    }
    
    /**
     * 检查库存是否充足
     */
    public boolean checkInventoryAvailability(String skuId, long quantity, String warehouseCode) {
        try {
            InventoryServiceProto.GetInventoryRequest request = InventoryServiceProto.GetInventoryRequest.newBuilder()
                    .setSkuId(skuId)
                    .setWarehouseCode(warehouseCode != null ? warehouseCode : "DEFAULT")
                    .build();
            
            InventoryServiceProto.InventoryResponse response = blockingStub.getInventory(request);
            InventoryServiceProto.Inventory inventory = response.getInventory();
            
            boolean isAvailable = inventory.getAvailableQuantity() >= quantity;
            
            logger.debug("库存检查: skuId={}, quantity={}, warehouseCode={}, available={}, availableQuantity={}", 
                        skuId, quantity, warehouseCode, isAvailable, inventory.getAvailableQuantity());
            return isAvailable;
            
        } catch (StatusRuntimeException e) {
            logger.error("库存检查失败: skuId={}, quantity={}, error={}", skuId, quantity, e.getMessage());
            return false; // 检查失败时返回库存不足
        }
    }
    
    /**
     * 批量检查库存
     */
    public InventoryServiceProto.GetInventoriesBySkuIdsResponse batchCheckInventory(List<String> skuIds, String warehouseCode) {
        try {
            InventoryServiceProto.GetInventoriesBySkuIdsRequest.Builder requestBuilder = 
                    InventoryServiceProto.GetInventoriesBySkuIdsRequest.newBuilder()
                    .addAllSkuIds(skuIds);
                    
            if (warehouseCode != null && !warehouseCode.isEmpty()) {
                requestBuilder.setWarehouseCode(warehouseCode);
            }
            
            InventoryServiceProto.GetInventoriesBySkuIdsRequest request = requestBuilder.build();
            InventoryServiceProto.GetInventoriesBySkuIdsResponse response = blockingStub.getInventoriesBySkuIds(request);
            
            logger.debug("批量库存检查成功: skuCount={}", skuIds.size());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("批量库存检查失败: skuCount={}, error={}", skuIds.size(), e.getMessage());
            throw new RuntimeException("批量库存检查失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 预留库存 (简化方法，用于订单流程)
     */
    public boolean reserveStock(String skuId, int quantity, String referenceType, String operatorId) {
        return reserveInventory(skuId, (long) quantity, "ORDER_" + System.currentTimeMillis(), operatorId, null);
    }
    
    /**
     * 预留库存
     */
    public boolean reserveInventory(String skuId, long quantity, String orderId, String operatorId, String warehouseCode) {
        try {
            InventoryServiceProto.ReserveStockRequest.Builder requestBuilder = InventoryServiceProto.ReserveStockRequest.newBuilder()
                    .setSkuId(skuId)
                    .setReserveQuantity(quantity)
                    .setReferenceId(orderId)
                    .setOperatorId(operatorId != null ? operatorId : "SYSTEM");
                    
            if (warehouseCode != null && !warehouseCode.isEmpty()) {
                requestBuilder.setWarehouseCode(warehouseCode);
            }
            
            InventoryServiceProto.ReserveStockRequest request = requestBuilder.build();
            InventoryServiceProto.ReserveStockResponse response = blockingStub.reserveStock(request);
            
            boolean success = response.getStatus().getSuccess();
            
            logger.debug("库存预留: skuId={}, quantity={}, orderId={}, success={}", 
                        skuId, quantity, orderId, success);
            return success;
            
        } catch (StatusRuntimeException e) {
            logger.error("库存预留失败: skuId={}, quantity={}, orderId={}, error={}", 
                        skuId, quantity, orderId, e.getMessage());
            return false;
        }
    }
    
    /**
     * 确认预留库存（实际出库）
     */
    public boolean confirmReservation(String skuId, long quantity, String orderId, String operatorId, String warehouseCode) {
        try {
            InventoryServiceProto.ConfirmStockRequest.Builder requestBuilder = InventoryServiceProto.ConfirmStockRequest.newBuilder()
                    .setSkuId(skuId)
                    .setConfirmQuantity(quantity)
                    .setReferenceId(orderId)
                    .setOperatorId(operatorId != null ? operatorId : "SYSTEM");
                    
            if (warehouseCode != null && !warehouseCode.isEmpty()) {
                requestBuilder.setWarehouseCode(warehouseCode);
            }
            
            InventoryServiceProto.ConfirmStockRequest request = requestBuilder.build();
            InventoryServiceProto.ConfirmStockResponse response = blockingStub.confirmStock(request);
            
            boolean success = response.getStatus().getSuccess();
            
            logger.debug("确认预留库存: skuId={}, quantity={}, orderId={}, success={}", 
                        skuId, quantity, orderId, success);
            return success;
            
        } catch (StatusRuntimeException e) {
            logger.error("确认预留库存失败: skuId={}, quantity={}, orderId={}, error={}", 
                        skuId, quantity, orderId, e.getMessage());
            return false;
        }
    }
    
    /**
     * 释放预留库存
     */
    public boolean releaseReservation(String skuId, long quantity, String orderId, String operatorId, String warehouseCode) {
        try {
            InventoryServiceProto.ReleaseStockRequest.Builder requestBuilder = InventoryServiceProto.ReleaseStockRequest.newBuilder()
                    .setSkuId(skuId)
                    .setReleaseQuantity(quantity)
                    .setReferenceId(orderId)
                    .setOperatorId(operatorId != null ? operatorId : "SYSTEM");
                    
            if (warehouseCode != null && !warehouseCode.isEmpty()) {
                requestBuilder.setWarehouseCode(warehouseCode);
            }
            
            InventoryServiceProto.ReleaseStockRequest request = requestBuilder.build();
            InventoryServiceProto.ReleaseStockResponse response = blockingStub.releaseStock(request);
            
            boolean success = response.getStatus().getSuccess();
            
            logger.debug("释放预留库存: skuId={}, quantity={}, orderId={}, success={}", 
                        skuId, quantity, orderId, success);
            return success;
            
        } catch (StatusRuntimeException e) {
            logger.error("释放预留库存失败: skuId={}, quantity={}, orderId={}, error={}", 
                        skuId, quantity, orderId, e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取库存信息 (返回Optional，用于订单流程)
     */
    public java.util.Optional<InventoryServiceProto.Inventory> getInventory(String skuId) {
        try {
            InventoryServiceProto.Inventory inventory = getInventoryWithWarehouse(skuId, null);
            return java.util.Optional.of(inventory);
        } catch (Exception e) {
            logger.warn("获取库存失败，返回空结果: skuId={}, error={}", skuId, e.getMessage());
            return java.util.Optional.empty();
        }
    }
    
    /**
     * 获取库存信息 (简化方法，默认仓库) - 内部使用
     */
    private InventoryServiceProto.Inventory getInventoryInternal(String skuId) {
        return getInventoryWithWarehouse(skuId, null);
    }
    
    /**
     * 获取库存信息
     */
    public InventoryServiceProto.Inventory getInventoryWithWarehouse(String skuId, String warehouseCode) {
        try {
            InventoryServiceProto.GetInventoryRequest.Builder requestBuilder = InventoryServiceProto.GetInventoryRequest.newBuilder()
                    .setSkuId(skuId);
                    
            if (warehouseCode != null && !warehouseCode.isEmpty()) {
                requestBuilder.setWarehouseCode(warehouseCode);
            }
            
            InventoryServiceProto.GetInventoryRequest request = requestBuilder.build();
            InventoryServiceProto.InventoryResponse response = blockingStub.getInventory(request);
            
            logger.debug("获取库存信息成功: skuId={}, warehouseCode={}", skuId, warehouseCode);
            return response.getInventory();
            
        } catch (StatusRuntimeException e) {
            logger.error("获取库存信息失败: skuId={}, warehouseCode={}, error={}", skuId, warehouseCode, e.getMessage());
            throw new RuntimeException("获取库存信息失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新库存数量
     */
    public InventoryServiceProto.UpdateStockResponse updateStock(String skuId, long changeQuantity, 
            InventoryServiceProto.StockChangeType changeType, String reason, String referenceId, String operatorId, String warehouseCode) {
        try {
            InventoryServiceProto.UpdateStockRequest.Builder requestBuilder = InventoryServiceProto.UpdateStockRequest.newBuilder()
                    .setSkuId(skuId)
                    .setChangeQuantity(changeQuantity)
                    .setChangeType(changeType)
                    .setReason(reason);
                    
            if (referenceId != null && !referenceId.isEmpty()) {
                requestBuilder.setReferenceId(referenceId);
            }
            
            if (operatorId != null && !operatorId.isEmpty()) {
                requestBuilder.setOperatorId(operatorId);
            }
            
            if (warehouseCode != null && !warehouseCode.isEmpty()) {
                requestBuilder.setWarehouseCode(warehouseCode);
            }
            
            InventoryServiceProto.UpdateStockRequest request = requestBuilder.build();
            InventoryServiceProto.UpdateStockResponse response = blockingStub.updateStock(request);
            
            logger.debug("更新库存成功: skuId={}, changeQuantity={}, changeType={}", skuId, changeQuantity, changeType);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("更新库存失败: skuId={}, changeQuantity={}, error={}", skuId, changeQuantity, e.getMessage());
            throw new RuntimeException("更新库存失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取库存变更记录
     */
    public InventoryServiceProto.GetStockRecordsResponse getStockRecords(String skuId, String referenceId, int pageNumber, int pageSize) {
        try {
            CommonProto.PageRequest pageRequest = CommonProto.PageRequest.newBuilder()
                    .setPageNumber(pageNumber)
                    .setPageSize(pageSize)
                    .build();
                    
            InventoryServiceProto.GetStockRecordsRequest.Builder requestBuilder = InventoryServiceProto.GetStockRecordsRequest.newBuilder()
                    .setPageRequest(pageRequest);
                    
            if (skuId != null && !skuId.isEmpty()) {
                requestBuilder.setSkuId(skuId);
            }
            
            if (referenceId != null && !referenceId.isEmpty()) {
                requestBuilder.setReferenceId(referenceId);
            }
            
            InventoryServiceProto.GetStockRecordsRequest request = requestBuilder.build();
            InventoryServiceProto.GetStockRecordsResponse response = blockingStub.getStockRecords(request);
            
            logger.debug("获取库存记录成功: skuId={}, referenceId={}", skuId, referenceId);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("获取库存记录失败: skuId={}, referenceId={}, error={}", skuId, referenceId, e.getMessage());
            throw new RuntimeException("获取库存记录失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 健康检查
     */
    public boolean isHealthy() {
        try {
            // 使用简单的获取库存记录请求作为健康检查
            CommonProto.PageRequest pageRequest = CommonProto.PageRequest.newBuilder()
                    .setPageNumber(1)
                    .setPageSize(1)
                    .build();
                    
            InventoryServiceProto.GetStockRecordsRequest request = InventoryServiceProto.GetStockRecordsRequest.newBuilder()
                    .setPageRequest(pageRequest)
                    .build();
            
            InventoryServiceProto.GetStockRecordsResponse response = blockingStub.getStockRecords(request);
            return response.getStatus().getSuccess();
        } catch (Exception e) {
            logger.warn("库存服务健康检查失败: {}", e.getMessage());
            return false;
        }
    }
}