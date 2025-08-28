package com.ecommerce.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.order.infrastructure.entity.OrderPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrderPlusMapper extends BaseMapper<OrderPO> {
    
    @Select("SELECT * FROM orders WHERE user_id = #{userId}")
    List<OrderPO> findByUserId(@Param("userId") String userId);
    
    @Select("SELECT * FROM orders WHERE status = #{status}")
    List<OrderPO> findByStatus(@Param("status") String status);
    
    @Select("SELECT * FROM orders WHERE user_id = #{userId} AND status = #{status}")
    List<OrderPO> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);
}