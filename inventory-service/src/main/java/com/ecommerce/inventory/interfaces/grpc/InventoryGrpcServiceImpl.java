package com.ecommerce.inventory.interfaces.grpc;

import com.ecommerce.inventory.proto.InventoryServiceGrpc;
import com.ecommerce.inventory.proto.InventoryServiceProto.*;
import com.ecommerce.common.proto.CommonProto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 库存gRPC服务实现 - 最小可编译版本
 * 暂时返回默认响应，后续需要完善业务逻辑
 */
@GrpcService
public class InventoryGrpcServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(InventoryGrpcServiceImpl.class);

    @Override
    public void getInventory(GetInventoryRequest request, StreamObserver<InventoryResponse> responseObserver) {
        try {
            // 创建默认库存信息
            Inventory inventory = Inventory.newBuilder()
                    .setSkuId(request.getSkuId())
                    .setWarehouseCode(request.hasWarehouseCode() ? request.getWarehouseCode() : "DEFAULT")
                    .setAvailableQuantity(0)
                    .setReservedQuantity(0)
                    .setTotalQuantity(0)
                    .setSafeStock(0)
                    .setMaxStock(0)
                    .setStockLevel(StockLevel.OUT_OF_STOCK)
                    .build();

            InventoryResponse response = InventoryResponse.newBuilder()
                    .setInventory(inventory)
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("获取库存失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("获取库存失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getInventoriesBySkuIds(GetInventoriesBySkuIdsRequest request, StreamObserver<GetInventoriesBySkuIdsResponse> responseObserver) {
        try {
            GetInventoriesBySkuIdsResponse response = GetInventoriesBySkuIdsResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("批量获取库存失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("批量获取库存失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateStock(UpdateStockRequest request, StreamObserver<UpdateStockResponse> responseObserver) {
        try {
            UpdateStockResponse response = UpdateStockResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("更新库存失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("更新库存失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void reserveStock(ReserveStockRequest request, StreamObserver<ReserveStockResponse> responseObserver) {
        try {
            ReserveStockResponse response = ReserveStockResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("预占库存失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("预占库存失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void releaseStock(ReleaseStockRequest request, StreamObserver<ReleaseStockResponse> responseObserver) {
        try {
            ReleaseStockResponse response = ReleaseStockResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("释放库存失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("释放库存失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void confirmStock(ConfirmStockRequest request, StreamObserver<ConfirmStockResponse> responseObserver) {
        try {
            ConfirmStockResponse response = ConfirmStockResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("确认库存失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("确认库存失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getStockRecords(GetStockRecordsRequest request, StreamObserver<GetStockRecordsResponse> responseObserver) {
        try {
            GetStockRecordsResponse response = GetStockRecordsResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("获取库存记录失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("获取库存记录失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void stockCheck(StockCheckRequest request, StreamObserver<StockCheckResponse> responseObserver) {
        try {
            StockCheckResponse response = StockCheckResponse.newBuilder()
                    .setStatus(createSuccessStatus())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("库存盘点失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("库存盘点失败: " + e.getMessage())
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