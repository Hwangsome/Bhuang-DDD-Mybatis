package com.ecommerce.user.interfaces.converter;

import com.ecommerce.common.proto.CommonProto;
import com.ecommerce.user.domain.entity.User;
import com.ecommerce.user.domain.valueobject.*;
import com.ecommerce.user.proto.UserServiceProto.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Proto与Domain对象转换器
 * 职责：处理gRPC Proto对象与DDD领域对象之间的转换
 * 原则：确保领域对象不依赖外部协议格式
 */
@Component
public class UserProtoConverter {

    /**
     * 领域用户对象转换为Proto用户对象
     */
    public com.ecommerce.user.proto.UserServiceProto.User toProtoUser(User domainUser) {
        var builder = com.ecommerce.user.proto.UserServiceProto.User.newBuilder()
                .setUserId(domainUser.getUserId().getValue())
                .setUsername(domainUser.getUsername())
                .setEmail(domainUser.getEmail().getValue())
                .setPhone(domainUser.getPhone().getFullNumber())
                .setFirstName(domainUser.getFirstName())
                .setLastName(domainUser.getLastName())
                .setStatus(convertDomainStatusToProto(domainUser.getStatus()))
                .setType(convertDomainTypeToProto(domainUser.getType()));

        // 处理可选字段
        if (domainUser.getAvatarUrl() != null) {
            builder.setAvatarUrl(domainUser.getAvatarUrl());
        }
        
        if (domainUser.getDefaultAddress() != null) {
            builder.setDefaultAddress(toProtoAddress(domainUser.getDefaultAddress()));
        }
        
        if (domainUser.getCreatedAt() != null) {
            builder.setCreatedAt(toProtoTimestamp(domainUser.getCreatedAt()));
        }
        
        if (domainUser.getUpdatedAt() != null) {
            builder.setUpdatedAt(toProtoTimestamp(domainUser.getUpdatedAt()));
        }
        
        if (domainUser.getCreatedBy() != null) {
            builder.setCreatedBy(domainUser.getCreatedBy());
        }
        
        if (domainUser.getUpdatedBy() != null) {
            builder.setUpdatedBy(domainUser.getUpdatedBy());
        }

        return builder.build();
    }

    /**
     * Proto创建用户请求转换为领域对象创建参数
     */
    public User fromProtoCreateRequest(CreateUserRequest request) {
        Email email = Email.of(request.getEmail());
        Phone phone = Phone.of(request.getPhone());
        User.UserType type = convertProtoTypeToDomain(request.getType());
        
        User user = User.create(
                request.getUsername(),
                email,
                phone,
                request.getFirstName(),
                request.getLastName(),
                type
        );
        
        // 设置默认地址
        if (request.hasDefaultAddress()) {
            user.setDefaultAddress(fromProtoAddress(request.getDefaultAddress()));
        }
        
        return user;
    }

    /**
     * Proto地址转换为领域地址
     */
    public Address fromProtoAddress(CommonProto.Address protoAddress) {
        return Address.builder()
                .country(protoAddress.getCountry())
                .province(protoAddress.getProvince())
                .city(protoAddress.getCity())
                .district(protoAddress.getDistrict())
                .street(protoAddress.getStreet())
                .postalCode(protoAddress.getPostalCode())
                .contactName(protoAddress.getContactName())
                .contactPhone(protoAddress.getContactPhone())
                .build();
    }

    /**
     * 领域地址转换为Proto地址
     */
    public CommonProto.Address toProtoAddress(Address domainAddress) {
        var builder = CommonProto.Address.newBuilder()
                .setCountry(domainAddress.getCountry())
                .setProvince(domainAddress.getProvince())
                .setCity(domainAddress.getCity())
                .setStreet(domainAddress.getStreet())
                .setContactName(domainAddress.getContactName())
                .setContactPhone(domainAddress.getContactPhone());
        
        if (domainAddress.getDistrict() != null) {
            builder.setDistrict(domainAddress.getDistrict());
        }
        
        if (domainAddress.getPostalCode() != null) {
            builder.setPostalCode(domainAddress.getPostalCode());
        }
        
        return builder.build();
    }

    /**
     * LocalDateTime转换为Proto Timestamp
     */
    public CommonProto.Timestamp toProtoTimestamp(LocalDateTime dateTime) {
        Instant instant = dateTime.toInstant(ZoneOffset.UTC);
        return CommonProto.Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    /**
     * Proto Timestamp转换为LocalDateTime
     */
    public LocalDateTime fromProtoTimestamp(CommonProto.Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * 用户列表转换为Proto用户列表
     */
    public List<com.ecommerce.user.proto.UserServiceProto.User> toProtoUserList(List<User> domainUsers) {
        return domainUsers.stream()
                .map(this::toProtoUser)
                .collect(Collectors.toList());
    }

    /**
     * 创建响应状态
     */
    public CommonProto.ResponseStatus createSuccessStatus() {
        return CommonProto.ResponseStatus.newBuilder()
                .setCode(200)
                .setMessage("Success")
                .setSuccess(true)
                .build();
    }

    /**
     * 创建错误响应状态
     */
    public CommonProto.ResponseStatus createErrorStatus(int code, String message) {
        return CommonProto.ResponseStatus.newBuilder()
                .setCode(code)
                .setMessage(message)
                .setSuccess(false)
                .build();
    }

    /**
     * 领域用户状态转换为Proto用户状态
     */
    private UserStatus convertDomainStatusToProto(User.UserStatus domainStatus) {
        return switch (domainStatus) {
            case ACTIVE -> UserStatus.USER_ACTIVE;
            case INACTIVE -> UserStatus.USER_INACTIVE;
            case SUSPENDED -> UserStatus.USER_SUSPENDED;
            case DELETED -> UserStatus.USER_DELETED;
        };
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
     * 领域用户类型转换为Proto用户类型
     */
    private UserType convertDomainTypeToProto(User.UserType domainType) {
        return switch (domainType) {
            case CUSTOMER -> UserType.CUSTOMER;
            case VIP_CUSTOMER -> UserType.VIP_CUSTOMER;
            case ADMIN -> UserType.ADMIN;
            case SUPER_ADMIN -> UserType.SUPER_ADMIN;
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