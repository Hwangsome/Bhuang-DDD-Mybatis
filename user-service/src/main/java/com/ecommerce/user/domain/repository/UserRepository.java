package com.ecommerce.user.domain.repository;

import com.ecommerce.user.domain.entity.User;
import com.ecommerce.user.domain.valueobject.UserId;
import com.ecommerce.user.domain.valueobject.Email;
import com.ecommerce.user.domain.valueobject.Phone;
import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口 - DDD仓储模式
 * 职责：定义用户聚合的持久化契约，屏蔽底层存储细节
 * 原则：面向领域模型设计，而非面向数据库表结构
 */
public interface UserRepository {
    
    /**
     * 保存用户（新增或更新）
     */
    User save(User user);
    
    /**
     * 根据用户ID查找用户
     */
    Optional<User> findById(UserId userId);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(Email email);
    
    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(Phone phone);
    
    /**
     * 批量根据ID查找用户
     */
    List<User> findByIds(List<UserId> userIds);
    
    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(User.UserStatus status);
    
    /**
     * 根据类型查找用户
     */
    List<User> findByType(User.UserType type);
    
    /**
     * 搜索用户 - 支持用户名、邮箱、手机号模糊查询
     */
    List<User> searchUsers(String keyword, User.UserStatus status, 
                          User.UserType type, int offset, int limit);
    
    /**
     * 统计用户数量
     */
    long countByStatus(User.UserStatus status);
    
    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否已存在
     */
    boolean existsByEmail(Email email);
    
    /**
     * 检查手机号是否已存在
     */
    boolean existsByPhone(Phone phone);
    
    /**
     * 删除用户（物理删除，谨慎使用）
     */
    void deleteById(UserId userId);
    
    /**
     * 批量更新用户状态
     */
    int updateStatusBatch(List<UserId> userIds, User.UserStatus status);
}