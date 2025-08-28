package com.ecommerce.order.domain.service;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.repository.OrderRepository;
import com.ecommerce.order.domain.valueobject.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderDomainService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    /**
     * 创建订单
     */
    public Order createOrder(String userId, Money totalAmount) {
        Order order = Order.create(
            UserId.of(userId),
            totalAmount
        );
        
        return orderRepository.save(order);
    }
    
    /**
     * 查询订单
     */
    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(OrderId.of(orderId));
    }
    
    /**
     * 根据用户ID查询订单
     */
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}