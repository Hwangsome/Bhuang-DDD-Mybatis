package com.ecommerce.notification.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.notification.infrastructure.entity.NotificationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface NotificationPlusMapper extends BaseMapper<NotificationPO> {
    
    @Select("SELECT * FROM notification WHERE user_id = #{userId}")
    List<NotificationPO> findByUserId(@Param("userId") String userId);
    
    @Select("SELECT * FROM notification WHERE status = #{status}")
    List<NotificationPO> findByStatus(@Param("status") String status);
    
    @Select("SELECT * FROM notification WHERE type = #{type}")
    List<NotificationPO> findByType(@Param("type") String type);
    
    @Select("SELECT * FROM notification WHERE channel = #{channel}")
    List<NotificationPO> findByChannel(@Param("channel") String channel);
    
    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND status = #{status}")
    List<NotificationPO> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);
}