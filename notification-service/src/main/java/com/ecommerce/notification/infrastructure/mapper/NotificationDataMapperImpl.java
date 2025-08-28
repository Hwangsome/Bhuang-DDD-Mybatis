package com.ecommerce.notification.infrastructure.mapper;

import com.ecommerce.notification.domain.entity.Notification;
import com.ecommerce.notification.domain.entity.NotificationStatus;
import com.ecommerce.notification.domain.valueobject.NotificationChannel;
import com.ecommerce.notification.domain.valueobject.NotificationId;
import com.ecommerce.notification.domain.valueobject.NotificationType;
import com.ecommerce.notification.domain.valueobject.UserId;
import com.ecommerce.notification.infrastructure.entity.NotificationPO;
import org.springframework.stereotype.Component;

@Component
public class NotificationDataMapperImpl implements NotificationDataMapper {

    @Override
    public NotificationPO notificationToNotificationPO(Notification notification) {
        if (notification == null) {
            return null;
        }
        
        NotificationPO notificationPO = new NotificationPO();
        notificationPO.setNotificationId(notification.getId() != null ? notification.getId().getValue() : null);
        notificationPO.setUserId(notification.getUserId() != null ? notification.getUserId().getValue() : null);
        notificationPO.setType(notification.getType() != null ? notification.getType().name() : null);
        notificationPO.setChannel(notification.getChannel() != null ? notification.getChannel().name() : null);
        notificationPO.setTitle(notification.getTitle());
        notificationPO.setContent(notification.getContent());
        notificationPO.setStatus(notification.getStatus() != null ? notification.getStatus().name() : null);
        notificationPO.setSentAt(notification.getSentAt());
        
        return notificationPO;
    }

    @Override
    public Notification notificationPOToNotification(NotificationPO notificationPO) {
        if (notificationPO == null) {
            return null;
        }
        
        // Use create method and reflection to set additional fields
        Notification notification = Notification.create(
            UserId.of(notificationPO.getUserId()),
            NotificationType.valueOf(notificationPO.getType()),
            NotificationChannel.valueOf(notificationPO.getChannel()),
            notificationPO.getTitle(),
            notificationPO.getContent(),
            "default@example.com" // default recipient
        );
        
        // Set additional fields using reflection
        try {
            java.lang.reflect.Field idField = Notification.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(notification, NotificationId.of(notificationPO.getNotificationId()));
            
            java.lang.reflect.Field statusField = Notification.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(notification, NotificationStatus.valueOf(notificationPO.getStatus()));
            
            if (notificationPO.getSentAt() != null) {
                java.lang.reflect.Field sentAtField = Notification.class.getDeclaredField("sentAt");
                sentAtField.setAccessible(true);
                sentAtField.set(notification, notificationPO.getSentAt());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to map NotificationPO to Notification", e);
        }
        
        return notification;
    }
}