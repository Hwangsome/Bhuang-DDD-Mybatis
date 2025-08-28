package com.ecommerce.notification.infrastructure.mapper;

import com.ecommerce.notification.domain.entity.Notification;
import com.ecommerce.notification.infrastructure.entity.NotificationPO;

public interface NotificationDataMapper {
    
    NotificationPO notificationToNotificationPO(Notification notification);
    
    Notification notificationPOToNotification(NotificationPO notificationPO);
}