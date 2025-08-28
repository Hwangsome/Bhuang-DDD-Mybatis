package com.ecommerce.user.infrastructure.repository;

import com.ecommerce.user.domain.entity.User;
import com.ecommerce.user.domain.repository.UserRepository;
import com.ecommerce.user.domain.valueobject.*;
import com.ecommerce.user.infrastructure.mapper.UserDataMapper;
import com.ecommerce.user.infrastructure.mapper.UserDataObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户仓储实现 - DDD仓储模式实现
 * 职责：实现领域仓储接口，处理User聚合与数据存储的转换
 * 原则：领域对象与数据对象分离，保持领域模型纯净
 */
@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl implements UserRepository {
    
    private final UserDataMapper userDataMapper;
    private final ObjectMapper objectMapper;
    
    public UserRepositoryImpl(UserDataMapper userDataMapper, ObjectMapper objectMapper) {
        this.userDataMapper = userDataMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserDataObject dataObject = convertToDataObject(user);
        
        // 判断是新增还是更新
        UserDataObject existing = userDataMapper.selectById(dataObject.getUserId());
        if (existing == null) {
            userDataMapper.insert(dataObject);
        } else {
            userDataMapper.updateById(dataObject);
        }
        
        return user;
    }

    @Override
    public Optional<User> findById(UserId userId) {
        UserDataObject dataObject = userDataMapper.selectById(userId.getValue());
        return Optional.ofNullable(dataObject)
                .map(this::convertToDomainObject);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        UserDataObject dataObject = userDataMapper.selectByUsername(username);
        return Optional.ofNullable(dataObject)
                .map(this::convertToDomainObject);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        UserDataObject dataObject = userDataMapper.selectByEmail(email.getValue());
        return Optional.ofNullable(dataObject)
                .map(this::convertToDomainObject);
    }

    @Override
    public Optional<User> findByPhone(Phone phone) {
        UserDataObject dataObject = userDataMapper.selectByPhone(phone.getFullNumber());
        return Optional.ofNullable(dataObject)
                .map(this::convertToDomainObject);
    }

    @Override
    public List<User> findByIds(List<UserId> userIds) {
        List<String> stringIds = userIds.stream()
                .map(UserId::getValue)
                .collect(Collectors.toList());
        
        List<UserDataObject> dataObjects = userDataMapper.selectByIds(stringIds);
        return dataObjects.stream()
                .map(this::convertToDomainObject)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByStatus(User.UserStatus status) {
        List<UserDataObject> dataObjects = userDataMapper.findByStatus(status.name());
        return dataObjects.stream()
                .map(this::convertToDomainObject)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByType(User.UserType type) {
        List<UserDataObject> dataObjects = userDataMapper.findByType(type.name());
        return dataObjects.stream()
                .map(this::convertToDomainObject)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> searchUsers(String keyword, User.UserStatus status, 
                                 User.UserType type, int offset, int limit) {
        String statusStr = status != null ? status.name() : null;
        String typeStr = type != null ? type.name() : null;
        
        List<UserDataObject> dataObjects = userDataMapper.searchUsers(
                keyword, statusStr, typeStr, offset, limit);
        
        return dataObjects.stream()
                .map(this::convertToDomainObject)
                .collect(Collectors.toList());
    }

    @Override
    public long countByStatus(User.UserStatus status) {
        return userDataMapper.countByStatus(status.name());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDataMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userDataMapper.existsByEmail(email.getValue());
    }

    @Override
    public boolean existsByPhone(Phone phone) {
        return userDataMapper.existsByPhone(phone.getFullNumber());
    }

    @Override
    @Transactional
    public void deleteById(UserId userId) {
        userDataMapper.deleteById(userId.getValue());
    }

    @Override
    @Transactional
    public int updateStatusBatch(List<UserId> userIds, User.UserStatus status) {
        List<String> stringIds = userIds.stream()
                .map(UserId::getValue)
                .collect(Collectors.toList());
        
        return userDataMapper.updateStatusBatch(stringIds, status.name(), LocalDateTime.now());
    }

    /**
     * 领域对象转换为数据对象
     */
    private UserDataObject convertToDataObject(User user) {
        UserDataObject dataObject = new UserDataObject();
        dataObject.setUserId(user.getUserId().getValue());
        dataObject.setUsername(user.getUsername());
        dataObject.setEmail(user.getEmail().getValue());
        dataObject.setPhone(user.getPhone().getValue());
        dataObject.setPhoneCountryCode(user.getPhone().getCountryCode());
        dataObject.setFirstName(user.getFirstName());
        dataObject.setLastName(user.getLastName());
        dataObject.setAvatarUrl(user.getAvatarUrl());
        dataObject.setStatus(user.getStatus().name());
        dataObject.setType(user.getType().name());
        dataObject.setCreatedAt(user.getCreatedAt());
        dataObject.setUpdatedAt(user.getUpdatedAt());
        dataObject.setCreatedBy(user.getCreatedBy());
        dataObject.setUpdatedBy(user.getUpdatedBy());
        
        // 地址对象分拆存储
        if (user.getDefaultAddress() != null) {
            Address address = user.getDefaultAddress();
            dataObject.setAddressCountry(address.getCountry());
            dataObject.setAddressProvince(address.getProvince());
            dataObject.setAddressCity(address.getCity());
            dataObject.setAddressDistrict(address.getDistrict());
            dataObject.setAddressStreet(address.getStreet());
            dataObject.setAddressPostalCode(address.getPostalCode());
            dataObject.setAddressContactName(address.getContactName());
            dataObject.setAddressContactPhone(address.getContactPhone());
        }
        
        return dataObject;
    }

    /**
     * 数据对象转换为领域对象
     */
    private User convertToDomainObject(UserDataObject dataObject) {
        UserId userId = UserId.of(dataObject.getUserId());
        Email email = Email.of(dataObject.getEmail());
        
        // 重构手机号，根据国家代码
        Phone phone;
        if (dataObject.getPhoneCountryCode() != null) {
            phone = Phone.ofInternational(dataObject.getPhoneCountryCode() + dataObject.getPhone().substring(dataObject.getPhoneCountryCode().length()));
        } else {
            phone = Phone.of(dataObject.getPhone());
        }
        
        User.UserStatus status = User.UserStatus.valueOf(dataObject.getStatus());
        User.UserType type = User.UserType.valueOf(dataObject.getType());
        
        // 分拆字段重构地址对象
        Address address = null;
        if (dataObject.getAddressCountry() != null) {
            address = Address.builder()
                    .country(dataObject.getAddressCountry())
                    .province(dataObject.getAddressProvince())
                    .city(dataObject.getAddressCity())
                    .district(dataObject.getAddressDistrict())
                    .street(dataObject.getAddressStreet())
                    .postalCode(dataObject.getAddressPostalCode())
                    .contactName(dataObject.getAddressContactName())
                    .contactPhone(dataObject.getAddressContactPhone())
                    .build();
        }
        
        return User.rebuild(
                userId,
                dataObject.getUsername(),
                email,
                phone,
                dataObject.getFirstName(),
                dataObject.getLastName(),
                dataObject.getAvatarUrl(),
                status,
                type,
                address,
                dataObject.getCreatedAt(),
                dataObject.getUpdatedAt(),
                dataObject.getCreatedBy(),
                dataObject.getUpdatedBy()
        );
    }

}