package com.ecommerce.order.domain.repository;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.valueobject.OrderId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    
    Order save(Order order);
    
    Optional<Order> findById(OrderId orderId);
    
    List<Order> findByUserId(String userId);
    
    void delete(OrderId orderId);
}