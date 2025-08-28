package com.ecommerce.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ecommerce.order.infrastructure.entity.OrderItemPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItemPO> {
    
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItemPO> findByOrderId(@Param("orderId") String orderId);
    
    @Select("SELECT * FROM order_item WHERE product_id = #{productId}")
    List<OrderItemPO> findByProductId(@Param("productId") String productId);
    
    @Select("SELECT * FROM order_item WHERE sku_id = #{skuId}")
    List<OrderItemPO> findBySkuId(@Param("skuId") String skuId);
}