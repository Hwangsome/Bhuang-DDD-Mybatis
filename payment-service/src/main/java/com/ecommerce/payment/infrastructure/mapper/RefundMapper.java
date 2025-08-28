package com.ecommerce.payment.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.payment.infrastructure.entity.RefundPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface RefundMapper extends BaseMapper<RefundPO> {
    
    @Select("SELECT * FROM refund WHERE payment_id = #{paymentId}")
    List<RefundPO> findByPaymentId(@Param("paymentId") String paymentId);
    
    @Select("SELECT * FROM refund WHERE status = #{status}")
    List<RefundPO> findByStatus(@Param("status") String status);
}