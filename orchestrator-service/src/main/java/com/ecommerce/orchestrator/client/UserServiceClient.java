package com.ecommerce.orchestrator.client;

import com.ecommerce.orchestrator.config.GrpcClientConfig;
import com.ecommerce.user.proto.UserServiceGrpc;
import com.ecommerce.user.proto.UserServiceProto;
import com.ecommerce.common.proto.CommonProto;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务gRPC客户端 - Orchestrator层服务调用
 * 职责：封装用户服务的gRPC调用，提供统一的客户端接口
 * 特性：连接池管理、超时控制、异常处理、熔断降级
 */
@Component
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    
    private final ManagedChannel channel;
    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;
    private final UserServiceGrpc.UserServiceFutureStub futureStub;
    
    public UserServiceClient(GrpcClientConfig grpcClientConfig) {
        this.channel = grpcClientConfig.userServiceChannel();
        this.blockingStub = UserServiceGrpc.newBlockingStub(channel);
        this.futureStub = UserServiceGrpc.newFutureStub(channel);
    }

    /**
     * 创建用户 - 同步调用
     */
    public Optional<UserServiceProto.User> createUser(UserServiceProto.CreateUserRequest request) {
        try {
            logger.debug("创建用户请求: username={}", request.getUsername());
            
            UserServiceProto.UserResponse response = blockingStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .createUser(request);
            
            if (response.getStatus().getSuccess()) {
                logger.info("用户创建成功: userId={}", response.getUser().getUserId());
                return Optional.of(response.getUser());
            } else {
                logger.warn("用户创建失败: {}", response.getStatus().getMessage());
                return Optional.empty();
            }
            
        } catch (StatusRuntimeException e) {
            logger.error("用户服务调用异常: {}", e.getStatus().getDescription(), e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("用户创建异常", e);
            return Optional.empty();
        }
    }

    /**
     * 获取用户 - 同步调用
     */
    public Optional<UserServiceProto.User> getUser(String userId) {
        try {
            logger.debug("获取用户: userId={}", userId);
            
            UserServiceProto.GetUserRequest request = UserServiceProto.GetUserRequest.newBuilder()
                    .setUserId(userId)
                    .build();
            
            UserServiceProto.UserResponse response = blockingStub
                    .withDeadlineAfter(3, TimeUnit.SECONDS)
                    .getUser(request);
            
            if (response.getStatus().getSuccess()) {
                return Optional.of(response.getUser());
            } else {
                logger.warn("获取用户失败: {}", response.getStatus().getMessage());
                return Optional.empty();
            }
            
        } catch (StatusRuntimeException e) {
            logger.error("用户服务调用异常: {}", e.getStatus().getDescription(), e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("获取用户异常", e);
            return Optional.empty();
        }
    }

    /**
     * 批量获取用户 - 同步调用
     */
    public List<UserServiceProto.User> getUsersByIds(List<String> userIds) {
        try {
            logger.debug("批量获取用户: userIds={}", userIds);
            
            UserServiceProto.GetUsersByIdsRequest request = UserServiceProto.GetUsersByIdsRequest.newBuilder()
                    .addAllUserIds(userIds)
                    .build();
            
            UserServiceProto.GetUsersByIdsResponse response = blockingStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .getUsersByIds(request);
            
            if (response.getStatus().getSuccess()) {
                return response.getUsersList();
            } else {
                logger.warn("批量获取用户失败: {}", response.getStatus().getMessage());
                return List.of();
            }
            
        } catch (StatusRuntimeException e) {
            logger.error("用户服务调用异常: {}", e.getStatus().getDescription(), e);
            return List.of();
        } catch (Exception e) {
            logger.error("批量获取用户异常", e);
            return List.of();
        }
    }

    /**
     * 更新用户 - 同步调用
     */
    public Optional<UserServiceProto.User> updateUser(UserServiceProto.UpdateUserRequest request) {
        try {
            logger.debug("更新用户: userId={}", request.getUserId());
            
            UserServiceProto.UserResponse response = blockingStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .updateUser(request);
            
            if (response.getStatus().getSuccess()) {
                logger.info("用户更新成功: userId={}", response.getUser().getUserId());
                return Optional.of(response.getUser());
            } else {
                logger.warn("用户更新失败: {}", response.getStatus().getMessage());
                return Optional.empty();
            }
            
        } catch (StatusRuntimeException e) {
            logger.error("用户服务调用异常: {}", e.getStatus().getDescription(), e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("用户更新异常", e);
            return Optional.empty();
        }
    }

    /**
     * 用户认证 - 同步调用
     */
    public Optional<UserServiceProto.User> authenticateUser(String usernameOrEmail, String password) {
        try {
            logger.debug("用户认证: usernameOrEmail={}", usernameOrEmail);
            
            UserServiceProto.AuthenticateUserRequest request = UserServiceProto.AuthenticateUserRequest.newBuilder()
                    .setUsernameOrEmail(usernameOrEmail)
                    .setPassword(password)
                    .build();
            
            UserServiceProto.AuthenticateUserResponse response = blockingStub
                    .withDeadlineAfter(3, TimeUnit.SECONDS)
                    .authenticateUser(request);
            
            if (response.getAuthenticated() && response.getStatus().getSuccess()) {
                logger.info("用户认证成功: userId={}", response.getUser().getUserId());
                return Optional.of(response.getUser());
            } else {
                logger.warn("用户认证失败: {}", response.getStatus().getMessage());
                return Optional.empty();
            }
            
        } catch (StatusRuntimeException e) {
            logger.error("用户服务调用异常: {}", e.getStatus().getDescription(), e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("用户认证异常", e);
            return Optional.empty();
        }
    }

    /**
     * 搜索用户 - 同步调用
     */
    public List<UserServiceProto.User> searchUsers(UserServiceProto.SearchUsersRequest request) {
        try {
            logger.debug("搜索用户: keyword={}", request.hasUsername() ? request.getUsername() : "全部");
            
            UserServiceProto.SearchUsersResponse response = blockingStub
                    .withDeadlineAfter(10, TimeUnit.SECONDS)
                    .searchUsers(request);
            
            if (response.getStatus().getSuccess()) {
                return response.getUsersList();
            } else {
                logger.warn("搜索用户失败: {}", response.getStatus().getMessage());
                return List.of();
            }
            
        } catch (StatusRuntimeException e) {
            logger.error("用户服务调用异常: {}", e.getStatus().getDescription(), e);
            return List.of();
        } catch (Exception e) {
            logger.error("搜索用户异常", e);
            return List.of();
        }
    }

    /**
     * 异步获取用户 - 用于并行调用场景
     */
    public CompletableFuture<Optional<UserServiceProto.User>> getUserAsync(String userId) {
        logger.debug("异步获取用户: userId={}", userId);
        
        UserServiceProto.GetUserRequest request = UserServiceProto.GetUserRequest.newBuilder()
                .setUserId(userId)
                .build();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                UserServiceProto.UserResponse response = futureStub
                        .withDeadlineAfter(3, TimeUnit.SECONDS)
                        .getUser(request)
                        .get(3, TimeUnit.SECONDS);
                
                if (response.getStatus().getSuccess()) {
                    return Optional.of(response.getUser());
                } else {
                    logger.warn("异步获取用户失败: {}", response.getStatus().getMessage());
                    return Optional.<UserServiceProto.User>empty();
                }
                
            } catch (Exception e) {
                logger.error("异步获取用户异常", e);
                return Optional.<UserServiceProto.User>empty();
            }
        });
    }

    /**
     * 检查服务健康状态
     */
    public boolean isHealthy() {
        try {
            // 使用一个轻量级的调用来检查服务状态
            UserServiceProto.GetUserRequest request = UserServiceProto.GetUserRequest.newBuilder()
                    .setUserId("health-check")
                    .build();
            
            blockingStub
                    .withDeadlineAfter(1, TimeUnit.SECONDS)
                    .getUser(request);
            
            return true;
            
        } catch (Exception e) {
            logger.debug("用户服务健康检查失败: {}", e.getMessage());
            return false;
        }
    }
}