package com.ecommerce.order.infrastructure.mapper;

import com.ecommerce.order.domain.entity.Order;
import com.ecommerce.order.domain.entity.OrderStatus;
import com.ecommerce.order.domain.valueobject.Money;
import com.ecommerce.order.domain.valueobject.OrderId;
import com.ecommerce.order.domain.valueobject.UserId;
import com.ecommerce.order.infrastructure.entity.OrderPO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class OrderDataMapperImpl implements OrderDataMapper {

    @Override
    public OrderPO orderToOrderPO(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderPO orderPO = new OrderPO();
        orderPO.setOrderId(order.getOrderId() != null ? order.getOrderId().getValue() : null);
        orderPO.setUserId(order.getUserId() != null ? order.getUserId().getValue() : null);
        orderPO.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount().getAmount() : null);
        orderPO.setCurrency(order.getTotalAmount() != null ? order.getTotalAmount().getCurrency().getCurrencyCode() : null);
        orderPO.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        orderPO.setOrderDate(LocalDateTime.now());
        
        return orderPO;
    }

    @Override
    public Order orderPOToOrder(OrderPO orderPO) {
        if (orderPO == null) {
            return null;
        }
        
        // Use create method and reflection to set additional fields
        java.util.Currency currency = java.util.Currency.getInstance(orderPO.getCurrency());
        Order order = Order.create(
            UserId.of(orderPO.getUserId()),
            Money.of(orderPO.getTotalAmount(), currency)
        );
        
        // Set additional fields using reflection
        try {
            java.lang.reflect.Field idField = Order.class.getDeclaredField("orderId");
            idField.setAccessible(true);
            idField.set(order, OrderId.of(orderPO.getOrderId()));
            
            java.lang.reflect.Field statusField = Order.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(order, OrderStatus.valueOf(orderPO.getStatus()));
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to map OrderPO to Order", e);
        }
        
        return order;
    }
}