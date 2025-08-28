package com.ecommerce.user.interfaces.grpc;

import com.ecommerce.user.domain.entity.User;
import com.ecommerce.user.domain.repository.UserRepository;
import com.ecommerce.user.domain.valueobject.*;
import com.ecommerce.user.interfaces.converter.UserProtoConverter;
import com.ecommerce.user.proto.UserServiceGrpc;
import com.ecommerce.user.proto.UserServiceProto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户gRPC服务实现 - DDD应用服务层
 * 职责：处理gRPC请求，协调领域对象完成业务操作
 * 原则：薄应用层，主要负责协调和转换，业务逻辑在领域层
 */
@GrpcService
public class UserGrpcServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(UserGrpcServiceImpl.class);
    
    private final UserRepository userRepository;
    private final UserProtoConverter protoConverter;
    
    public UserGrpcServiceImpl(UserRepository userRepository, UserProtoConverter protoConverter) {
        this.userRepository = userRepository;
        this.protoConverter = protoConverter;
    }

    @Override
    @Transactional
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            // 验证用户名唯一性
            if (userRepository.existsByUsername(request.getUsername())) {
                UserResponse response = UserResponse.newBuilder()
                        .setStatus(protoConverter.createErrorStatus(400, "用户名已存在"))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            // 验证邮箱唯一性
            Email email = Email.of(request.getEmail());
            if (userRepository.existsByEmail(email)) {
                UserResponse response = UserResponse.newBuilder()
                        .setStatus(protoConverter.createErrorStatus(400, "邮箱已存在"))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            // 验证手机号唯一性
            Phone phone = Phone.of(request.getPhone());
            if (userRepository.existsByPhone(phone)) {
                UserResponse response = UserResponse.newBuilder()
                        .setStatus(protoConverter.createErrorStatus(400, "手机号已存在"))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            // 创建用户领域对象
            User user = protoConverter.fromProtoCreateRequest(request);
            
            // 保存用户
            User savedUser = userRepository.save(user);
            
            // 构建响应
            UserResponse response = UserResponse.newBuilder()
                    .setUser(protoConverter.toProtoUser(savedUser))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.info("用户创建成功: userId={}, username={}", savedUser.getUserId().getValue(), savedUser.getUsername());
            
        } catch (IllegalArgumentException e) {
            logger.warn("用户创建参数错误: {}", e.getMessage());
            UserResponse response = UserResponse.newBuilder()
                    .setStatus(protoConverter.createErrorStatus(400, "参数错误: " + e.getMessage()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("用户创建失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("用户创建失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UserId userId = UserId.of(request.getUserId());
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                UserResponse response = UserResponse.newBuilder()
                        .setStatus(protoConverter.createErrorStatus(404, "用户不存在"))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            User user = userOpt.get();
            UserResponse response = UserResponse.newBuilder()
                    .setUser(protoConverter.toProtoUser(user))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("获取用户失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("获取用户失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getUsersByIds(GetUsersByIdsRequest request, StreamObserver<GetUsersByIdsResponse> responseObserver) {
        try {
            List<UserId> userIds = request.getUserIdsList().stream()
                    .map(UserId::of)
                    .collect(Collectors.toList());
            
            List<User> users = userRepository.findByIds(userIds);
            
            GetUsersByIdsResponse response = GetUsersByIdsResponse.newBuilder()
                    .addAllUsers(protoConverter.toProtoUserList(users))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("批量获取用户失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("批量获取用户失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UserId userId = UserId.of(request.getUserId());
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                UserResponse response = UserResponse.newBuilder()
                        .setStatus(protoConverter.createErrorStatus(404, "用户不存在"))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            User user = userOpt.get();
            
            // 更新基础信息
            if (request.hasFirstName() || request.hasLastName() || request.hasAvatarUrl()) {
                String firstName = request.hasFirstName() ? request.getFirstName() : user.getFirstName();
                String lastName = request.hasLastName() ? request.getLastName() : user.getLastName();
                String avatarUrl = request.hasAvatarUrl() ? request.getAvatarUrl() : user.getAvatarUrl();
                user.updateBasicInfo(firstName, lastName, avatarUrl);
            }
            
            // 更新邮箱
            if (request.hasEmail()) {
                Email newEmail = Email.of(request.getEmail());
                if (userRepository.existsByEmail(newEmail) && !user.getEmail().equals(newEmail)) {
                    UserResponse response = UserResponse.newBuilder()
                            .setStatus(protoConverter.createErrorStatus(400, "邮箱已存在"))
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                    return;
                }
                user.updateEmail(newEmail);
            }
            
            // 更新手机号
            if (request.hasPhone()) {
                Phone newPhone = Phone.of(request.getPhone());
                if (userRepository.existsByPhone(newPhone) && !user.getPhone().equals(newPhone)) {
                    UserResponse response = UserResponse.newBuilder()
                            .setStatus(protoConverter.createErrorStatus(400, "手机号已存在"))
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                    return;
                }
                user.updatePhone(newPhone);
            }
            
            // 更新状态
            if (request.hasStatus()) {
                switch (request.getStatus()) {
                    case USER_ACTIVE -> user.activate();
                    case USER_INACTIVE -> user.deactivate();
                    case USER_SUSPENDED -> user.suspend();
                    case USER_DELETED -> user.delete();
                }
            }
            
            // 更新默认地址
            if (request.hasDefaultAddress()) {
                Address address = protoConverter.fromProtoAddress(request.getDefaultAddress());
                user.setDefaultAddress(address);
            }
            
            // 保存更新
            User updatedUser = userRepository.save(user);
            
            UserResponse response = UserResponse.newBuilder()
                    .setUser(protoConverter.toProtoUser(updatedUser))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.info("用户更新成功: userId={}", userId.getValue());
            
        } catch (IllegalArgumentException e) {
            logger.warn("用户更新参数错误: {}", e.getMessage());
            UserResponse response = UserResponse.newBuilder()
                    .setStatus(protoConverter.createErrorStatus(400, "参数错误: " + e.getMessage()))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            logger.error("用户更新失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("用户更新失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void authenticateUser(AuthenticateUserRequest request, StreamObserver<AuthenticateUserResponse> responseObserver) {
        try {
            // 简化版认证逻辑 - 实际项目中需要密码加密验证
            Optional<User> userOpt = Optional.empty();
            
            String usernameOrEmail = request.getUsernameOrEmail();
            
            // 判断是用户名还是邮箱
            if (usernameOrEmail.contains("@")) {
                userOpt = userRepository.findByEmail(Email.of(usernameOrEmail));
            } else {
                userOpt = userRepository.findByUsername(usernameOrEmail);
            }
            
            if (userOpt.isEmpty()) {
                AuthenticateUserResponse response = AuthenticateUserResponse.newBuilder()
                        .setAuthenticated(false)
                        .setStatus(protoConverter.createErrorStatus(404, "用户不存在"))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            User user = userOpt.get();
            
            // 检查用户状态
            if (!user.isActive()) {
                AuthenticateUserResponse response = AuthenticateUserResponse.newBuilder()
                        .setAuthenticated(false)
                        .setStatus(protoConverter.createErrorStatus(403, "用户账号已被禁用"))
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                return;
            }
            
            // TODO: 实际项目中需要验证密码
            // boolean passwordValid = passwordEncoder.matches(request.getPassword(), user.getPassword());
            
            // 简化版 - 总是返回认证成功
            AuthenticateUserResponse response = AuthenticateUserResponse.newBuilder()
                    .setAuthenticated(true)
                    .setUser(protoConverter.toProtoUser(user))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.info("用户认证成功: userId={}", user.getUserId().getValue());
            
        } catch (Exception e) {
            logger.error("用户认证失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("用户认证失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void searchUsers(SearchUsersRequest request, StreamObserver<SearchUsersResponse> responseObserver) {
        try {
            String keyword = request.hasUsername() ? request.getUsername() : null;
            User.UserStatus status = request.hasStatus() ? convertProtoStatusToDomain(request.getStatus()) : null;
            User.UserType type = request.hasType() ? convertProtoTypeToDomain(request.getType()) : null;
            
            int pageNumber = request.getPageRequest().getPageNumber();
            int pageSize = request.getPageRequest().getPageSize();
            int offset = (pageNumber - 1) * pageSize;
            
            List<User> users = userRepository.searchUsers(keyword, status, type, offset, pageSize);
            
            SearchUsersResponse response = SearchUsersResponse.newBuilder()
                    .addAllUsers(protoConverter.toProtoUserList(users))
                    .setStatus(protoConverter.createSuccessStatus())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            logger.error("搜索用户失败", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("搜索用户失败: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Proto用户状态转换为领域用户状态
     */
    private User.UserStatus convertProtoStatusToDomain(UserStatus protoStatus) {
        return switch (protoStatus) {
            case USER_ACTIVE -> User.UserStatus.ACTIVE;
            case USER_INACTIVE -> User.UserStatus.INACTIVE;
            case USER_SUSPENDED -> User.UserStatus.SUSPENDED;
            case USER_DELETED -> User.UserStatus.DELETED;
            default -> throw new IllegalArgumentException("未知的用户状态: " + protoStatus);
        };
    }

    /**
     * Proto用户类型转换为领域用户类型
     */
    private User.UserType convertProtoTypeToDomain(UserType protoType) {
        return switch (protoType) {
            case CUSTOMER -> User.UserType.CUSTOMER;
            case VIP_CUSTOMER -> User.UserType.VIP_CUSTOMER;
            case ADMIN -> User.UserType.ADMIN;
            case SUPER_ADMIN -> User.UserType.SUPER_ADMIN;
            default -> throw new IllegalArgumentException("未知的用户类型: " + protoType);
        };
    }
}