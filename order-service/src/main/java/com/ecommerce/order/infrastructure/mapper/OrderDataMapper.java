package com.ecommerce.order.infrastructure.mapper;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.infrastructure.entity.OrderPO;

public interface OrderDataMapper {
    
    OrderPO orderToOrderPO(Order order);
    
    Order orderPOToOrder(OrderPO orderPO);
}