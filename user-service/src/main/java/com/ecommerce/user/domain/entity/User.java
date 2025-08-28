package com.ecommerce.user.domain.entity;

import com.ecommerce.user.domain.valueobject.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户聚合根 - DDD核心实体
 * 职责：管理用户基础信息，封装用户相关业务规则
 * 边界：只处理用户自身的业务逻辑，不涉及其他聚合
 */
public class User {
    
    // 聚合根标识
    private UserId userId;
    
    // 基础信息
    private String username;
    private Email email;
    private Phone phone;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    
    // 业务状态
    private UserStatus status;
    private UserType type;
    
    // 地址信息
    private Address defaultAddress;
    
    // 审计信息
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * 私有构造函数 - 通过工厂方法创建
     */
    private User() {
        // 防止直接实例化
    }

    /**
     * 创建新用户 - 工厂方法
     */
    public static User create(String username, Email email, Phone phone, 
                             String firstName, String lastName, UserType type) {
        User user = new User();
        user.userId = UserId.generate();
        user.username = Objects.requireNonNull(username, "用户名不能为空");
        user.email = Objects.requireNonNull(email, "邮箱不能为空");
        user.phone = Objects.requireNonNull(phone, "手机号不能为空");
        user.firstName = Objects.requireNonNull(firstName, "名字不能为空");
        user.lastName = Objects.requireNonNull(lastName, "姓氏不能为空");
        user.type = Objects.requireNonNull(type, "用户类型不能为空");
        user.status = UserStatus.ACTIVE; // 新用户默认激活
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
        
        user.validateUserData();
        return user;
    }

    /**
     * 从持久化数据重建用户 - 仓储层使用
     */
    public static User rebuild(UserId userId, String username, Email email, Phone phone,
                              String firstName, String lastName, String avatarUrl,
                              UserStatus status, UserType type, Address defaultAddress,
                              LocalDateTime createdAt, LocalDateTime updatedAt,
                              String createdBy, String updatedBy) {
        User user = new User();
        user.userId = userId;
        user.username = username;
        user.email = email;
        user.phone = phone;
        user.firstName = firstName;
        user.lastName = lastName;
        user.avatarUrl = avatarUrl;
        user.status = status;
        user.type = type;
        user.defaultAddress = defaultAddress;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        user.createdBy = createdBy;
        user.updatedBy = updatedBy;
        return user;
    }

    /**
     * 更新基础信息
     */
    public void updateBasicInfo(String firstName, String lastName, String avatarUrl) {
        if (firstName != null && !firstName.trim().isEmpty()) {
            this.firstName = firstName.trim();
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            this.lastName = lastName.trim();
        }
        if (avatarUrl != null) {
            this.avatarUrl = avatarUrl;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新邮箱
     */
    public void updateEmail(Email newEmail) {
        if (!this.email.equals(newEmail)) {
            this.email = Objects.requireNonNull(newEmail, "邮箱不能为空");
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 更新手机号
     */
    public void updatePhone(Phone newPhone) {
        if (!this.phone.equals(newPhone)) {
            this.phone = Objects.requireNonNull(newPhone, "手机号不能为空");
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 设置默认地址
     */
    public void setDefaultAddress(Address address) {
        this.defaultAddress = address;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 激活用户
     */
    public void activate() {
        if (this.status == UserStatus.DELETED) {
            throw new IllegalStateException("已删除的用户无法激活");
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停用用户
     */
    public void deactivate() {
        if (this.status == UserStatus.DELETED) {
            throw new IllegalStateException("已删除的用户无法停用");
        }
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 暂停用户
     */
    public void suspend() {
        if (this.status == UserStatus.DELETED) {
            throw new IllegalStateException("已删除的用户无法暂停");
        }
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 删除用户 (逻辑删除)
     */
    public void delete() {
        this.status = UserStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 升级为VIP用户
     */
    public void upgradeToVip() {
        if (this.type == UserType.CUSTOMER) {
            this.type = UserType.VIP_CUSTOMER;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 判断用户是否活跃
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    /**
     * 判断用户是否为VIP
     */
    public boolean isVip() {
        return this.type == UserType.VIP_CUSTOMER;
    }

    /**
     * 判断用户是否为管理员
     */
    public boolean isAdmin() {
        return this.type == UserType.ADMIN || this.type == UserType.SUPER_ADMIN;
    }

    /**
     * 获取用户全名
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * 验证用户数据
     */
    private void validateUserData() {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("用户名长度必须在3-50个字符之间");
        }
        if (firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
    }

    // Getters
    public UserId getUserId() { return userId; }
    public String getUsername() { return username; }
    public Email getEmail() { return email; }
    public Phone getPhone() { return phone; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAvatarUrl() { return avatarUrl; }
    public UserStatus getStatus() { return status; }
    public UserType getType() { return type; }
    public Address getDefaultAddress() { return defaultAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email=" + email +
                ", status=" + status +
                ", type=" + type +
                '}';
    }

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE,     // 活跃
        INACTIVE,   // 非活跃
        SUSPENDED,  // 暂停
        DELETED     // 已删除
    }

    /**
     * 用户类型枚举
     */
    public enum UserType {
        CUSTOMER,       // 普通用户
        VIP_CUSTOMER,   // VIP用户
        ADMIN,          // 管理员
        SUPER_ADMIN     // 超级管理员
    }
}