package com.ecommerce.notification.domain.repository;

import com.ecommerce.notification.domain.entity.Notification;
import com.ecommerce.notification.domain.valueobject.NotificationId;
import com.ecommerce.notification.domain.valueobject.UserId;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    
    Notification save(Notification notification);
    
    Optional<Notification> findById(NotificationId notificationId);
    
    List<Notification> findByUserId(UserId userId);
    
    void deleteById(NotificationId notificationId);
}