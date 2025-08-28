package com.ecommerce.order.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.repository.OrderRepository;
import com.ecommerce.order.domain.valueobject.OrderId;
import com.ecommerce.order.infrastructure.entity.OrderPO;
import com.ecommerce.order.infrastructure.mapper.OrderDataMapper;
import com.ecommerce.order.infrastructure.mapper.OrderPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    
    @Autowired
    private OrderPlusMapper orderPlusMapper;
    
    @Autowired
    private OrderDataMapper orderDataMapper;

    @Override
    public Order save(Order order) {
        OrderPO orderPO = orderDataMapper.orderToOrderPO(order);
        if (orderPO.getId() == null) {
            orderPlusMapper.insert(orderPO);
        } else {
            orderPlusMapper.updateById(orderPO);
        }
        return orderDataMapper.orderPOToOrder(orderPO);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        LambdaQueryWrapper<OrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderPO::getOrderId, orderId.getValue());
        OrderPO orderPO = orderPlusMapper.selectOne(queryWrapper);
        return orderPO != null ? 
            Optional.of(orderDataMapper.orderPOToOrder(orderPO)) : 
            Optional.empty();
    }

    @Override
    public List<Order> findByUserId(String userId) {
        List<OrderPO> orderPOs = orderPlusMapper.findByUserId(userId);
        return orderPOs.stream()
            .map(orderDataMapper::orderPOToOrder)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(OrderId orderId) {
        LambdaQueryWrapper<OrderPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderPO::getOrderId, orderId.getValue());
        orderPlusMapper.delete(queryWrapper);
    }
}