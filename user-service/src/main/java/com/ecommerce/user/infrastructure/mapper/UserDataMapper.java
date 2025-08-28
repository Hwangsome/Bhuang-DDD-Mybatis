package com.ecommerce.user.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户数据映射器 - MyBatis数据访问层
 * 职责：定义用户数据的CRUD操作，支持DDD领域驱动设计
 * 特性：乐观锁、软删除、MyBatis-Plus集成、XML配置
 */
@Mapper
public interface UserDataMapper extends BaseMapper<UserDataObject> {
    
    /**
     * 根据用户名查找用户（XML映射）
     */
    UserDataObject selectByUsername(@Param("username") String username);
    
    /**
     * 根据邮箱查找用户（XML映射）
     */
    UserDataObject selectByEmail(@Param("email") String email);
    
    /**
     * 根据手机号查找用户（XML映射）
     */
    UserDataObject selectByPhone(@Param("phone") String phone);
    
    /**
     * 批量根据ID查找用户（XML映射）
     */
    List<UserDataObject> selectByIds(@Param("list") List<String> userIds);
    
    /**
     * 检查用户名是否存在（XML映射）
     */
    boolean existsByUsername(@Param("username") String username);
    
    /**
     * 检查邮箱是否存在（XML映射）
     */
    boolean existsByEmail(@Param("email") String email);
    
    /**
     * 检查手机号是否存在（XML映射）
     */
    boolean existsByPhone(@Param("phone") String phone);
    
    /**
     * 搜索用户（XML映射）
     */
    List<UserDataObject> searchUsers(@Param("keyword") String keyword,
                                   @Param("status") String status,
                                   @Param("type") String type,
                                   @Param("offset") int offset,
                                   @Param("pageSize") int pageSize);
    
    /**
     * 根据状态查找用户（XML映射）
     */
    List<UserDataObject> findByStatus(@Param("status") String status);
    
    /**
     * 根据类型查找用户（XML映射）
     */
    List<UserDataObject> findByType(@Param("type") String type);
    
    /**
     * 根据状态统计用户数量（XML映射）
     */
    long countByStatus(@Param("status") String status);
    
    /**
     * 批量更新用户状态（XML映射）
     */
    int updateStatusBatch(@Param("userIds") List<String> userIds, 
                         @Param("status") String status,
                         @Param("updatedAt") java.time.LocalDateTime updatedAt);
    
    /**
     * 软删除用户（XML映射）
     */
    int softDelete(@Param("userId") String userId);
}

