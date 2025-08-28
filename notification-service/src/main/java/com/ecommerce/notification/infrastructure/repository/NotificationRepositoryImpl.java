package com.ecommerce.notification.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.notification.domain.entity.Notification;
import com.ecommerce.notification.domain.repository.NotificationRepository;
import com.ecommerce.notification.domain.valueobject.NotificationId;
import com.ecommerce.notification.domain.valueobject.UserId;
import com.ecommerce.notification.infrastructure.entity.NotificationPO;
import com.ecommerce.notification.infrastructure.mapper.NotificationDataMapper;
import com.ecommerce.notification.infrastructure.mapper.NotificationPlusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    
    @Autowired
    private NotificationPlusMapper notificationPlusMapper;
    
    @Autowired
    private NotificationDataMapper notificationDataMapper;

    @Override
    public Notification save(Notification notification) {
        NotificationPO notificationPO = notificationDataMapper.notificationToNotificationPO(notification);
        if (notificationPO.getId() == null) {
            notificationPlusMapper.insert(notificationPO);
        } else {
            notificationPlusMapper.updateById(notificationPO);
        }
        return notificationDataMapper.notificationPOToNotification(notificationPO);
    }

    @Override
    public Optional<Notification> findById(NotificationId notificationId) {
        LambdaQueryWrapper<NotificationPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NotificationPO::getNotificationId, notificationId.getValue());
        NotificationPO notificationPO = notificationPlusMapper.selectOne(queryWrapper);
        return notificationPO != null ? 
            Optional.of(notificationDataMapper.notificationPOToNotification(notificationPO)) : 
            Optional.empty();
    }

    @Override
    public List<Notification> findByUserId(UserId userId) {
        List<NotificationPO> notificationPOs = notificationPlusMapper.findByUserId(userId.getValue());
        return notificationPOs.stream()
            .map(notificationDataMapper::notificationPOToNotification)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(NotificationId notificationId) {
        LambdaQueryWrapper<NotificationPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NotificationPO::getNotificationId, notificationId.getValue());
        notificationPlusMapper.delete(queryWrapper);
    }
}