package com.ecommerce.payment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.payment.infrastructure.entity.PaymentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PaymentMapper extends BaseMapper<PaymentPO> {
    
    @Select("SELECT * FROM payment WHERE order_id = #{orderId}")
    List<PaymentPO> findByOrderId(@Param("orderId") String orderId);
    
    @Select("SELECT * FROM payment WHERE user_id = #{userId}")
    List<PaymentPO> findByUserId(@Param("userId") String userId);
    
    @Select("SELECT * FROM payment WHERE status = #{status}")
    List<PaymentPO> findByStatus(@Param("status") String status);
}