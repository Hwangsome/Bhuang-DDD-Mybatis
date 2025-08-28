package com.ecommerce.orchestrator.client;

import com.ecommerce.orchestrator.config.GrpcClientConfig;
import com.ecommerce.notification.proto.NotificationServiceGrpc;
import com.ecommerce.notification.proto.NotificationServiceProto;
import com.ecommerce.common.proto.CommonProto;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 通知服务gRPC客户端
 * 职责：与通知服务进行gRPC通信，提供通知相关操作
 */
@Component
public class NotificationServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceClient.class);
    
    private final ManagedChannel channel;
    private final NotificationServiceGrpc.NotificationServiceBlockingStub blockingStub;
    
    public NotificationServiceClient(GrpcClientConfig grpcClientConfig) {
        this.channel = grpcClientConfig.notificationServiceChannel();
        this.blockingStub = NotificationServiceGrpc.newBlockingStub(channel);
    }
    
    /**
     * 发送通知
     */
    public NotificationServiceProto.SendNotificationResponse sendNotification(NotificationServiceProto.SendNotificationRequest request) {
        try {
            NotificationServiceProto.SendNotificationResponse response = blockingStub.sendNotification(request);
            logger.debug("发送通知成功: notificationId={}", response.getNotificationId());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("发送通知失败: error={}", e.getMessage());
            throw new RuntimeException("发送通知失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送邮件
     */
    public NotificationServiceProto.SendEmailResponse sendEmail(String userId, String recipient, String subject, String htmlContent) {
        try {
            NotificationServiceProto.SendEmailRequest request = NotificationServiceProto.SendEmailRequest.newBuilder()
                    .setUserId(userId)
                    .setToEmail(recipient)
                    .setSubject(subject)
                    .setHtmlContent(htmlContent)
                    .build();
            
            NotificationServiceProto.SendEmailResponse response = blockingStub.sendEmail(request);
            logger.debug("发送邮件成功: recipient={}, subject={}", recipient, subject);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("发送邮件失败: recipient={}, error={}", recipient, e.getMessage());
            throw new RuntimeException("发送邮件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送短信
     */
    public NotificationServiceProto.SendSMSResponse sendSMS(String userId, String phoneNumber, String message) {
        try {
            NotificationServiceProto.SendSMSRequest request = NotificationServiceProto.SendSMSRequest.newBuilder()
                    .setUserId(userId)
                    .setPhoneNumber(phoneNumber)
                    .setMessage(message)
                    .build();
            
            NotificationServiceProto.SendSMSResponse response = blockingStub.sendSMS(request);
            logger.debug("发送短信成功: phoneNumber={}", phoneNumber);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("发送短信失败: phoneNumber={}, error={}", phoneNumber, e.getMessage());
            throw new RuntimeException("发送短信失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 发送推送通知
     */
    public NotificationServiceProto.SendPushNotificationResponse sendPushNotification(String userId, String deviceToken, String title, String body) {
        try {
            NotificationServiceProto.SendPushNotificationRequest request = NotificationServiceProto.SendPushNotificationRequest.newBuilder()
                    .setUserId(userId)
                    .setDeviceToken(deviceToken)
                    .setTitle(title)
                    .setBody(body)
                    .build();
            
            NotificationServiceProto.SendPushNotificationResponse response = blockingStub.sendPushNotification(request);
            logger.debug("发送推送通知成功: userId={}, title={}", userId, title);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("发送推送通知失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("发送推送通知失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量发送通知
     */
    public NotificationServiceProto.BatchSendNotificationResponse batchSendNotification(
            NotificationServiceProto.BatchSendNotificationRequest request) {
        try {
            NotificationServiceProto.BatchSendNotificationResponse response = blockingStub.batchSendNotification(request);
            logger.debug("批量发送通知成功: count={}", request.getUserIdsCount());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("批量发送通知失败: error={}", e.getMessage());
            throw new RuntimeException("批量发送通知失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取通知历史
     */
    public NotificationServiceProto.GetNotificationHistoryResponse getNotificationHistory(
            String userId, int pageNumber, int pageSize) {
        try {
            CommonProto.PageRequest pageRequest = CommonProto.PageRequest.newBuilder()
                    .setPageNumber(pageNumber)
                    .setPageSize(pageSize)
                    .build();
                    
            NotificationServiceProto.GetNotificationHistoryRequest.Builder requestBuilder = 
                    NotificationServiceProto.GetNotificationHistoryRequest.newBuilder()
                    .setPageRequest(pageRequest);
                    
            if (userId != null && !userId.isEmpty()) {
                requestBuilder.setUserId(userId);
            }
            
            NotificationServiceProto.GetNotificationHistoryRequest request = requestBuilder.build();
            NotificationServiceProto.GetNotificationHistoryResponse response = blockingStub.getNotificationHistory(request);
            
            logger.debug("获取通知历史成功: userId={}", userId);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("获取通知历史失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取通知历史失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取通知模板
     */
    public NotificationServiceProto.GetNotificationTemplateResponse getNotificationTemplate(String templateId) {
        try {
            NotificationServiceProto.GetNotificationTemplateRequest request = 
                    NotificationServiceProto.GetNotificationTemplateRequest.newBuilder()
                    .setTemplateId(templateId)
                    .build();
            
            NotificationServiceProto.GetNotificationTemplateResponse response = blockingStub.getNotificationTemplate(request);
            logger.debug("获取通知模板成功: templateId={}", templateId);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("获取通知模板失败: templateId={}, error={}", templateId, e.getMessage());
            throw new RuntimeException("获取通知模板失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建通知模板
     */
    public NotificationServiceProto.NotificationTemplateResponse createNotificationTemplate(
            NotificationServiceProto.CreateNotificationTemplateRequest request) {
        try {
            NotificationServiceProto.NotificationTemplateResponse response = blockingStub.createNotificationTemplate(request);
            logger.debug("创建通知模板成功: templateId={}", response.getTemplate().getTemplateId());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("创建通知模板失败: error={}", e.getMessage());
            throw new RuntimeException("创建通知模板失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取用户通知设置
     */
    public NotificationServiceProto.UserNotificationSettingsResponse getUserNotificationSettings(String userId) {
        try {
            NotificationServiceProto.GetUserNotificationSettingsRequest request = 
                    NotificationServiceProto.GetUserNotificationSettingsRequest.newBuilder()
                    .setUserId(userId)
                    .build();
            
            NotificationServiceProto.UserNotificationSettingsResponse response = blockingStub.getUserNotificationSettings(request);
            logger.debug("获取用户通知设置成功: userId={}", userId);
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("获取用户通知设置失败: userId={}, error={}", userId, e.getMessage());
            throw new RuntimeException("获取用户通知设置失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 更新用户通知设置
     */
    public NotificationServiceProto.UserNotificationSettingsResponse updateUserNotificationSettings(
            NotificationServiceProto.UpdateUserNotificationSettingsRequest request) {
        try {
            NotificationServiceProto.UserNotificationSettingsResponse response = blockingStub.updateUserNotificationSettings(request);
            logger.debug("更新用户通知设置成功: userId={}", request.getUserId());
            return response;
            
        } catch (StatusRuntimeException e) {
            logger.error("更新用户通知设置失败: userId={}, error={}", request.getUserId(), e.getMessage());
            throw new RuntimeException("更新用户通知设置失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 健康检查
     */
    public boolean isHealthy() {
        try {
            // 使用简单的获取通知历史请求作为健康检查
            CommonProto.PageRequest pageRequest = CommonProto.PageRequest.newBuilder()
                    .setPageNumber(1)
                    .setPageSize(1)
                    .build();
                    
            NotificationServiceProto.GetNotificationHistoryRequest request = 
                    NotificationServiceProto.GetNotificationHistoryRequest.newBuilder()
                    .setPageRequest(pageRequest)
                    .build();
            
            NotificationServiceProto.GetNotificationHistoryResponse response = blockingStub.getNotificationHistory(request);
            return response.getStatus().getSuccess();
        } catch (Exception e) {
            logger.warn("通知服务健康检查失败: {}", e.getMessage());
            return false;
        }
    }
}